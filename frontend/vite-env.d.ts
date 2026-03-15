declare module 'vite' {
  interface UserConfig {
    plugins?: unknown[]
    server?: { proxy?: Record<string, { target: string; changeOrigin?: boolean }> }
  }
  export function defineConfig(config: UserConfig): UserConfig
}

declare module '@vitejs/plugin-react' {
  export default function react(): unknown
}
