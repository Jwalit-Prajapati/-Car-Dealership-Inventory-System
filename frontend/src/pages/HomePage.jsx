import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { list } from '../api/vehicles';
import { BASE_URL } from '../api/client';
import VehicleGrid from '../components/VehicleGrid';

const FEATURED_COUNT = 3;

export default function HomePage() {
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    let cancelled = false;

    async function fetchFeatured() {
      setLoading(true);
      try {
        const data = await list({ page: 0, size: FEATURED_COUNT, sort: 'id,desc' });
        if (!cancelled) setVehicles(data.content);
      } catch {
        if (!cancelled) setVehicles([]);
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    fetchFeatured();
    return () => {
      cancelled = true;
    };
  }, []);

  const heroImage = vehicles.find((vehicle) => vehicle.imageUrl)?.imageUrl;

  return (
    <div
      className="relative min-h-screen bg-cover bg-center bg-fixed"
      style={{
        backgroundImage: heroImage
          ? `linear-gradient(to right, rgba(10,10,13,0.92), rgba(10,10,13,0.55)), url(${BASE_URL}${heroImage})`
          : 'linear-gradient(135deg, #0a0a0d, #1a0d0f)',
      }}
    >
      <section className="px-6 py-8">
        <div className="mx-auto max-w-7xl">
          <h1 className="max-w-2xl text-4xl font-bold text-text-primary sm:text-5xl">
            Find Your Perfect Car
          </h1>
          <p className="mt-3 max-w-xl text-lg text-text-secondary">Buy vehicles with confidence.</p>
        </div>
      </section>

      <section className="mx-auto max-w-7xl space-y-6 px-6 py-8">
        <h2 className="text-2xl font-semibold text-text-primary">Featured Listings</h2>
        <VehicleGrid vehicles={vehicles} loading={loading} onPurchase={() => navigate('/vehicles')} />
        <div className="flex justify-center">
          <button
            type="button"
            onClick={() => navigate('/vehicles')}
            className="rounded bg-[var(--accent)] px-6 py-2.5 text-sm font-semibold text-white hover:bg-[var(--accent-hover)]"
          >
            Browse Cars
          </button>
        </div>
      </section>
    </div>
  );
}
