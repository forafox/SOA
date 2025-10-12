/** @type {import('next').NextConfig} */
const nextConfig = {
  eslint: {
    ignoreDuringBuilds: false,
  },
  // output: 'export',
  trailingSlash: true,
  images: {
    unoptimized: true,
  },
  // Настраиваем правильные пути для сервера в зависимости от профиля
  assetPrefix: process.env.NEXT_PUBLIC_BASE_PATH || '',
  basePath: process.env.NEXT_PUBLIC_BASE_PATH || '',
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
