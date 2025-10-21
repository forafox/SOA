/** @type {import('next').NextConfig} */
const nextConfig = {
  eslint: {
    ignoreDuringBuilds: false,
  },
  output: 'standalone',
  trailingSlash: true,
  images: {
    unoptimized: true,
  },
  // Убираем basePath для Docker, чтобы API routes работали
  // assetPrefix: process.env.NODE_ENV === 'production' ? '/~s367268/soa' : '',
  // basePath: process.env.NODE_ENV === 'production' ? '/~s367268/soa' : '',
  // Убираем rewrites для статического экспорта
  // async rewrites() {
  //   return [
  //     {
  //       source: '/oscars/:path*',
  //       destination: 'http://localhost:8080/oscars/:path*',
  //     },
  //     {
  //       source: '/api/movies/:path*',
  //       destination: 'http://localhost:8081/api/movies/:path*',
  //     },
  //   ];
  // },
};

export default nextConfig;
