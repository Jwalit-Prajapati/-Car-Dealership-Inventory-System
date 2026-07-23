import { Component } from 'react';

export default class ErrorBoundary extends Component {
  state = { hasError: false };

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  componentDidCatch(error, info) {
    console.error('Unhandled UI error:', error, info);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex min-h-[60vh] flex-col items-center justify-center gap-3 bg-surface-0 p-6 text-center">
          <h1 className="text-xl font-semibold text-text-primary">Something went wrong.</h1>
          <p className="text-text-muted">Please refresh the page and try again.</p>
          <button
            type="button"
            onClick={() => window.location.assign('/')}
            className="rounded bg-surface-2 px-4 py-2 text-text-primary hover:bg-surface-3"
          >
            Back to home
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
