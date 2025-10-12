'use client';

import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { LogOut, LogIn, User } from 'lucide-react';
import oauth2AuthService, { OAuth2User } from '@/lib/oauth2-auth';

export function AuthComponent() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<OAuth2User | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    setIsAuthenticated(oauth2AuthService.isUserAuthenticated());
    setUser(oauth2AuthService.getCurrentUser());
  }, []);

  const handleLogin = async () => {
    setIsLoading(true);
    try {
      await oauth2AuthService.login();
    } catch (error) {
      console.error('Login failed:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = async () => {
    setIsLoading(true);
    try {
      await oauth2AuthService.logout();
      setIsAuthenticated(false);
      setUser(null);
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (isAuthenticated && user) {
    return (
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <User className="h-5 w-5" />
            Authenticated User
          </CardTitle>
          <CardDescription>
            You are logged in via OAuth2 SSO
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <p className="font-medium">{user.firstName} {user.lastName}</p>
            <p className="text-sm text-muted-foreground">{user.email}</p>
            <p className="text-sm text-muted-foreground">@{user.username}</p>
          </div>
          
          <div>
            <p className="text-sm font-medium mb-2">Roles:</p>
            <div className="flex flex-wrap gap-1">
              {user.roles.map((role) => (
                <Badge key={role} variant="secondary">
                  {role}
                </Badge>
              ))}
            </div>
          </div>
          
          <Button 
            onClick={handleLogout} 
            variant="outline" 
            className="w-full"
            disabled={isLoading}
          >
            <LogOut className="h-4 w-4 mr-2" />
            {isLoading ? 'Logging out...' : 'Logout'}
          </Button>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <LogIn className="h-5 w-5" />
          Authentication Required
        </CardTitle>
        <CardDescription>
          Please log in to access the SOA services
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Button 
          onClick={handleLogin} 
          className="w-full"
          disabled={isLoading}
        >
          <LogIn className="h-4 w-4 mr-2" />
          {isLoading ? 'Redirecting...' : 'Login with OAuth2 SSO'}
        </Button>
      </CardContent>
    </Card>
  );
}
