/** @type {import('next').NextConfig} */
const nextConfig = {
  eslint: {
    ignoreDuringBuilds: false,
  },
  async rewrites() {
    return [
      {
        source: '/oscars/:path*',
        destination: 'http://localhost:8080/oscars/:path*',
      },
      {
        source: '/api/movies/:path*',
        destination: 'http://localhost:8081/api/movies/:path*',
      },
    ];
  },
};

export default nextConfig;
