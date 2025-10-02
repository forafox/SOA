/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'export',
  trailingSlash: true,
  skipTrailingSlashRedirect: true,
  distDir: 'out',
  // Устанавливаем базовый путь для статических ресурсов в WildFly
  basePath: process.env.NODE_ENV === 'production' ? '/backend-oscars-0.0.1-SNAPSHOT' : '',
  assetPrefix: process.env.NODE_ENV === 'production' ? '/backend-oscars-0.0.1-SNAPSHOT' : '',
  images: {
    unoptimized: true
  },
  experimental: {
    missingSuspenseWithCSRBailout: false,
  }
}

module.exports = nextConfig
