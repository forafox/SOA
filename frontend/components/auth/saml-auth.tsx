'use client';

import { useEffect, useState } from 'react';
import { samlService, SamlUser } from '@/lib/saml-service';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { LogOut, User, Shield } from 'lucide-react';

interface SamlAuthProps {
  children: React.ReactNode;
}

export function SamlAuth({ children }: SamlAuthProps) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<SamlUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    console.log('SamlAuth useEffect - checking for SAML response');
    console.log('Current URL:', window.location.href);
    
    // Проверяем, есть ли SAML ответ в URL
    const urlParams = new URLSearchParams(window.location.search);
    const samlResponse = urlParams.get('SAMLResponse');
    
    console.log('SAML Response in URL:', samlResponse);
    
    if (samlResponse) {
      console.log('Processing SAML response...');
      // Обрабатываем SAML ответ
      samlService.handleSamlResponse().then((userData) => {
        console.log('SAML response processed, user data:', userData);
        if (userData) {
          setUser(userData);
          setIsAuthenticated(true);
        }
        setIsLoading(false);
        
        // Очищаем URL от SAML параметров
        window.history.replaceState({}, document.title, window.location.pathname);
      }).catch((error) => {
        console.error('Error processing SAML response:', error);
        setIsLoading(false);
      });
    } else {
      console.log('No SAML response, checking existing authentication');
      // Проверяем существующую аутентификацию
      const authenticated = samlService.isAuthenticated();
      const userData = samlService.getCurrentUser();
      
      console.log('Existing auth status:', authenticated, 'User:', userData);
      setIsAuthenticated(authenticated);
      setUser(userData);
      setIsLoading(false);
    }
  }, []);

  const handleLogin = () => {
    samlService.initiateLogin();
  };

  const handleLogout = () => {
    samlService.logout();
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900 mx-auto"></div>
          <p className="mt-2 text-sm text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <CardTitle className="text-2xl font-bold">SOA Application</CardTitle>
            <CardDescription>
              Please authenticate to access the application
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <Button onClick={handleLogin} className="w-full" size="lg">
              <Shield className="mr-2 h-4 w-4" />
              Login with SAML
            </Button>
            <p className="text-xs text-gray-500 text-center">
              You will be redirected to Keycloak for authentication
            </p>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header with user info */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-gray-900">SOA Application</h1>
            </div>
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <User className="h-4 w-4 text-gray-500" />
                <span className="text-sm font-medium text-gray-700">{user?.name}</span>
                {user?.roles && user.roles.length > 0 && (
                  <Badge variant="secondary" className="text-xs">
                    {user.roles[0]}
                  </Badge>
                )}
              </div>
              <Button variant="outline" size="sm" onClick={handleLogout}>
                <LogOut className="h-4 w-4 mr-2" />
                Logout
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Main content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {children}
      </main>
    </div>
  );
}
