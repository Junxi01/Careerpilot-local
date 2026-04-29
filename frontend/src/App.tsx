import React from 'react'

export function App() {
  return (
    <div style={styles.page}>
      <h1 style={styles.title}>CareerPilot Local</h1>
      <p style={styles.body}>
        Frontend scaffold (React + TypeScript + Vite). Business logic and API integration will be added next.
      </p>
      <p style={styles.body}>
        Backend scaffold endpoint:{' '}
        <code style={styles.code}>
          {import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'}/api/scaffold
        </code>
      </p>
    </div>
  )
}

const styles: Record<string, React.CSSProperties> = {
  page: {
    fontFamily:
      'ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, "Apple Color Emoji", "Segoe UI Emoji"',
    padding: 32,
    maxWidth: 800,
    margin: '0 auto',
    color: '#111827',
  },
  title: { fontSize: 28, margin: 0, marginBottom: 8 },
  body: { fontSize: 16, lineHeight: 1.5, marginTop: 8 },
  code: {
    padding: '2px 6px',
    borderRadius: 6,
    background: '#f3f4f6',
    border: '1px solid #e5e7eb',
  },
}

