// OAuth2/OIDC Authentication Service for Frontend
import CryptoJS from 'crypto-js';

export interface OAuth2Config {
  keycloakUrl: string;
  realm: string;
  clientId: string;
  redirectUri: string;
}

export interface OAuth2User {
  username: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  roles: string[];
  accessToken: string;
  refreshToken?: string;
}

class OAuth2AuthService {
  private config: OAuth2Config;
  private isAuthenticated: boolean = false;
  private user: OAuth2User | null = null;

  constructor() {
    this.config = {
      keycloakUrl: 'http://localhost:8082',
      realm: 'soa-realm',
      clientId: 'frontend-client',
      redirectUri: typeof window !== 'undefined' ? window.location.origin + '/callback' : 'http://localhost:3000/callback'
    };
    
    this.loadAuthState();
  }

  private loadAuthState(): void {
    if (typeof window === 'undefined') return;
    
    const authData = localStorage.getItem('oauth2_auth');
    if (authData) {
      try {
        const parsed = JSON.parse(authData);
        this.isAuthenticated = parsed.isAuthenticated;
        this.user = parsed.user;
      } catch (e) {
        console.error('Failed to parse auth data:', e);
        this.clearAuthState();
      }
    }
  }

  private saveAuthState(): void {
    if (typeof window === 'undefined') return;
    
    localStorage.setItem('oauth2_auth', JSON.stringify({
      isAuthenticated: this.isAuthenticated,
      user: this.user
    }));
  }

  private clearAuthState(): void {
    if (typeof window === 'undefined') return;
    
    localStorage.removeItem('oauth2_auth');
    this.isAuthenticated = false;
    this.user = null;
  }

  public async login(): Promise<void> {
    const authUrl = `${this.config.keycloakUrl}/realms/${this.config.realm}/protocol/openid-connect/auth` +
      `?client_id=${this.config.clientId}` +
      `&redirect_uri=${encodeURIComponent(this.config.redirectUri)}` +
      `&response_type=code` +
      `&scope=openid profile email` +
      `&state=${this.generateState()}`;
    
    // Redirect to Keycloak OAuth2 login
    window.location.href = authUrl;
  }

  public async logout(): Promise<void> {
    const logoutUrl = `${this.config.keycloakUrl}/realms/${this.config.realm}/protocol/openid-connect/logout` +
      `?client_id=${this.config.clientId}` +
      `&post_logout_redirect_uri=${encodeURIComponent(this.config.redirectUri)}`;
    
    this.clearAuthState();
    
    // Redirect to Keycloak OAuth2 logout
    window.location.href = logoutUrl;
  }

  public async handleOAuth2Callback(code: string): Promise<void> {
    try {
      // Exchange authorization code for tokens
      const tokenResponse = await this.exchangeCodeForTokens(code);
      
      // Get user info
      const userInfo = await this.getUserInfo(tokenResponse.access_token);
      
      const user: OAuth2User = {
        username: userInfo.preferred_username || userInfo.sub,
        email: userInfo.email,
        firstName: userInfo.given_name,
        lastName: userInfo.family_name,
        roles: userInfo.realm_access?.roles || [],
        accessToken: tokenResponse.access_token,
        refreshToken: tokenResponse.refresh_token
      };

      this.user = user;
      this.isAuthenticated = true;
      this.saveAuthState();
      
      console.log('OAuth2 authentication successful:', user);
    } catch (error) {
      console.error('Failed to process OAuth2 callback:', error);
      throw new Error('Authentication failed');
    }
  }

  private async exchangeCodeForTokens(code: string): Promise<{ access_token: string; refresh_token?: string }> {
    const tokenUrl = `${this.config.keycloakUrl}/realms/${this.config.realm}/protocol/openid-connect/token`;
    
    const response = await fetch(tokenUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: new URLSearchParams({
        grant_type: 'authorization_code',
        client_id: this.config.clientId,
        code: code,
        redirect_uri: this.config.redirectUri
      })
    });

    if (!response.ok) {
      throw new Error('Failed to exchange code for tokens');
    }

    return await response.json();
  }

  private async getUserInfo(accessToken: string): Promise<{ 
    sub: string; 
    preferred_username?: string; 
    email?: string; 
    given_name?: string; 
    family_name?: string; 
    realm_access?: { roles: string[] } 
  }> {
    const userInfoUrl = `${this.config.keycloakUrl}/realms/${this.config.realm}/protocol/openid-connect/userinfo`;
    
    const response = await fetch(userInfoUrl, {
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to get user info');
    }

    return await response.json();
  }

  private generateState(): string {
    return CryptoJS.lib.WordArray.random(32).toString();
  }

  public isUserAuthenticated(): boolean {
    return this.isAuthenticated;
  }

  public getCurrentUser(): OAuth2User | null {
    return this.user;
  }

  public hasRole(role: string): boolean {
    return this.user?.roles.includes(role) ?? false;
  }

  public getAuthHeaders(): Record<string, string> {
    if (!this.isAuthenticated || !this.user) {
      return {};
    }

    return {
      'Authorization': `Bearer ${this.user.accessToken}`
    };
  }
}

export const oauth2AuthService = new OAuth2AuthService();
export default oauth2AuthService;
