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
        <div className="flex min-h-[60vh] flex-col items-center justify-center gap-3 p-6 text-center">
          <h1 className="text-xl font-semibold text-slate-900">Something went wrong.</h1>
          <p className="text-slate-500">Please refresh the page and try again.</p>
          <button
            type="button"
            onClick={() => window.location.assign('/')}
            className="rounded bg-slate-900 px-4 py-2 text-white hover:bg-slate-800"
          >
            Back to home
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
