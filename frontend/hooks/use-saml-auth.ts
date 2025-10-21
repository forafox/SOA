'use client';

import { useState, useEffect } from 'react';
import { samlService, SamlUser } from '@/lib/saml-service';

export function useSamlAuth() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<SamlUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const checkAuth = () => {
      const authenticated = samlService.isAuthenticated();
      const userData = samlService.getCurrentUser();
      
      setIsAuthenticated(authenticated);
      setUser(userData);
      setIsLoading(false);
    };

    checkAuth();

    // Проверяем аутентификацию при изменении localStorage
    const handleStorageChange = (e: StorageEvent) => {
      if (e.key === 'saml_authenticated' || e.key === 'saml_user') {
        checkAuth();
      }
    };

    window.addEventListener('storage', handleStorageChange);
    
    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, []);

  const login = () => {
    samlService.initiateLogin();
  };

  const logout = () => {
    samlService.logout();
  };

  const hasRole = (role: string) => {
    return samlService.hasRole(role);
  };

  const isAdmin = () => {
    return samlService.isAdmin();
  };

  return {
    isAuthenticated,
    user,
    isLoading,
    login,
    logout,
    hasRole,
    isAdmin
  };
}
