package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.dto.VehicleResponseDTO;
import com.jwalit.inventory_system.dto.VehicleSearchRequest;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.exception.InvalidFileException;
import com.jwalit.inventory_system.mapper.VehicleMapper;
import com.jwalit.inventory_system.repository.VehicleRepository;
import com.jwalit.inventory_system.repository.VehicleSpecifications;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional
    public VehicleResponseDTO create(VehicleRequestDTO vehicleRequestDTO) {
        Vehicle vehicle = vehicleMapper.toEntity(vehicleRequestDTO);
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }

    public Page<VehicleResponseDTO> getVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable)
                .map(vehicleMapper::toResponseDto);
    }

    public Page<VehicleResponseDTO> searchVehicles(VehicleSearchRequest request,
                                                    Pageable pageable) {
        Specification<Vehicle> spec = Specification
                .where(VehicleSpecifications.hasMake(request.make()))
                .and(VehicleSpecifications.hasModel(request.model()))
                .and(VehicleSpecifications.hasCategory(request.category()))
                .and(VehicleSpecifications.hasMinimumPrice(request.minPrice()))
                .and(VehicleSpecifications.hasMaximumPrice(request.maxPrice()));

        return vehicleRepository.findAll(spec, pageable)
                .map(vehicleMapper::toResponseDto);
    }

    @Transactional
    public VehicleResponseDTO update(Long id, VehicleRequestDTO vehicleRequestDTO) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found with id: " + id));

        vehicleMapper.updateEntityFromDto(vehicleRequestDTO, vehicle);

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found with id: " + id));
        vehicleRepository.delete(vehicle);
    }

    @Transactional
    public VehicleResponseDTO updateImage(Long id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("No file was uploaded");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileException("Only JPEG, PNG, or WebP images are allowed");
        }

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found with id: " + id));

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            Files.createDirectories(uploadPath);

            deleteExistingImage(vehicle.getImageUrl(), uploadPath);

            String extension = switch (contentType) {
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> ".jpg";
            };
            String filename = UUID.randomUUID() + extension;
            Path target = uploadPath.resolve(filename);
            file.transferTo(target);

            vehicle.setImageUrl("/images/" + filename);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store uploaded image", e);
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }

    private void deleteExistingImage(String existingImageUrl, Path uploadPath) {
        if (existingImageUrl == null || !existingImageUrl.startsWith("/images/")) {
            return;
        }
        String existingFilename = existingImageUrl.substring("/images/".length());
        Path existingFile = uploadPath.resolve(existingFilename);
        try {
            Files.deleteIfExists(existingFile);
        } catch (IOException e) {
            log.warn("Failed to delete previous vehicle image {}", existingFile, e);
        }
    }

}
