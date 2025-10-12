"use client";

import { useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import oauth2AuthService from "@/lib/oauth2-auth";
import { toast } from "sonner";

export default function CallbackPage() {
  const [status, setStatus] = useState<'processing' | 'success' | 'error'>('processing');
  const [error, setError] = useState<string | null>(null);
  const searchParams = useSearchParams();
  const router = useRouter();

  useEffect(() => {
    const handleCallback = async () => {
      try {
        const code = searchParams.get('code');
        const error = searchParams.get('error');

        if (error) {
          setError(`OAuth2 Error: ${error}`);
          setStatus('error');
          return;
        }

        if (!code) {
          setError('No authorization code received');
          setStatus('error');
          return;
        }

        console.log('Processing OAuth2 callback with code:', code);
        
        // Обрабатываем OAuth2 callback
        await oauth2AuthService.handleOAuth2Callback(code);
        
        setStatus('success');
        toast.success("Successfully logged in via OAuth2!");
        
        // Перенаправляем на главную страницу
        setTimeout(() => {
          router.push('/');
        }, 2000);
        
      } catch (err) {
        console.error('OAuth2 callback error:', err);
        setError(err instanceof Error ? err.message : 'Authentication failed');
        setStatus('error');
      }
    };

    handleCallback();
  }, [searchParams, router]);

  if (status === 'processing') {
    return (
      <div className="flex h-screen bg-background items-center justify-center">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle>Processing Authentication...</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center justify-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
            </div>
            <p className="text-center mt-4">Please wait while we complete your login.</p>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (status === 'error') {
    return (
      <div className="flex h-screen bg-background items-center justify-center">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle className="text-red-600">Authentication Failed</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-red-600 mb-4">{error}</p>
            <Button 
              onClick={() => router.push('/')} 
              className="w-full"
            >
              Return to Home
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="flex h-screen bg-background items-center justify-center">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-green-600">Authentication Successful!</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-green-600 mb-4">You have been successfully logged in.</p>
          <p className="text-sm text-muted-foreground">Redirecting to the main page...</p>
        </CardContent>
      </Card>
    </div>
  );
}
