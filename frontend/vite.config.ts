import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  // vitest bundles its own older nested `vite`, whose Plugin type conflicts
  // with the top-level vite used by @vitejs/plugin-react (dual-package hazard).
  plugins: [react() as any],
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/setupTests.ts'],
    globals: false,
    css: true,
  },
})
