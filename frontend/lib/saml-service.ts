// SAML handled via browser redirect; no direct client import required

export interface SamlUser {
  name: string;
  email?: string;
  roles: string[];
  attributes: Record<string, any>;
}

export class SamlService {
  private keycloakUrl: string;
  private realm: string;
  private clientId: string;
  private redirectUri: string;

  constructor() {
    this.keycloakUrl = process.env.NEXT_PUBLIC_KEYCLOAK_URL || 'http://localhost:8082';
    this.realm = process.env.NEXT_PUBLIC_KEYCLOAK_REALM || 'soa-realm';
    this.clientId = process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_ID || 'frontend-saml';
    this.redirectUri = typeof window !== 'undefined' ? window.location.origin : 'http://localhost:3000';
  }

  /**
   * Инициирует SAML аутентификацию
   */
  public initiateLogin(): void {
    // Используем IDP-initiated SSO URL
    const loginUrl = `${this.keycloakUrl}/realms/${this.realm}/protocol/saml/clients/${this.clientId}`;
    window.location.href = loginUrl;
  }

  /**
   * Обрабатывает SAML ответ от Keycloak
   */
  public async handleSamlResponse(): Promise<SamlUser | null> {
    try {
      // Проверяем, есть ли SAML ответ в URL параметрах
      const urlParams = new URLSearchParams(window.location.search);
      const samlResponseParam = urlParams.get('SAMLResponse');
      
      if (!samlResponseParam) {
        // Если нет SAML ответа, проверяем, есть ли уже аутентифицированный пользователь
        const existingUser = this.getCurrentUser();
        if (existingUser) {
          return existingUser;
        }
        return null;
      }

      // Декодируем base64 SAML ответ
      const decodedResponse = atob(samlResponseParam);
      
      // Парсим XML (упрощенная версия)
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(decodedResponse, 'text/xml');
      
      // Извлекаем информацию о пользователе
      const nameId = xmlDoc.querySelector('saml\\:NameID, NameID')?.textContent;
      const attributes = xmlDoc.querySelectorAll('saml\\:Attribute, Attribute');
      
      const userAttributes: Record<string, any> = {};
      const roles: string[] = [];
      
      attributes.forEach(attr => {
        const name = attr.getAttribute('Name');
        const values = attr.querySelectorAll('saml\\:AttributeValue, AttributeValue');
        
        if (name) {
          const attributeValues = Array.from(values).map(v => v.textContent).filter(Boolean);
          
          if (name === 'Role' || name === 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role') {
            roles.push(...attributeValues);
          } else {
            userAttributes[name] = attributeValues.length === 1 ? attributeValues[0] : attributeValues;
          }
        }
      });

      // Если роли не найдены в атрибутах, попробуем найти их в других местах
      if (roles.length === 0) {
        const roleElements = xmlDoc.querySelectorAll('saml\\:Attribute[Name="Role"], saml\\:Attribute[Name="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role"]');
        roleElements.forEach(roleEl => {
          const roleValues = roleEl.querySelectorAll('saml\\:AttributeValue, AttributeValue');
          roleValues.forEach(roleVal => {
            if (roleVal.textContent) {
              roles.push(roleVal.textContent);
            }
          });
        });
      }

      const user: SamlUser = {
        name: nameId || 'Unknown User',
        email: userAttributes.email || userAttributes['http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress'],
        roles,
        attributes: userAttributes
      };

      // Сохраняем информацию о пользователе в localStorage
      localStorage.setItem('saml_user', JSON.stringify(user));
      localStorage.setItem('saml_authenticated', 'true');

      return user;
    } catch (error) {
      console.error('SAML response processing error:', error);
      return null;
    }
  }

  /**
   * Проверяет, аутентифицирован ли пользователь
   */
  public isAuthenticated(): boolean {
    if (typeof window === 'undefined') return false;
    return localStorage.getItem('saml_authenticated') === 'true';
  }

  /**
   * Получает информацию о текущем пользователе
   */
  public getCurrentUser(): SamlUser | null {
    if (typeof window === 'undefined') return null;
    
    const userStr = localStorage.getItem('saml_user');
    if (!userStr) return null;
    
    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  }

  /**
   * Выполняет logout
   */
  public logout(): void {
    if (typeof window === 'undefined') return;
    
    // Очищаем локальные данные
    localStorage.removeItem('saml_user');
    localStorage.removeItem('saml_authenticated');
    
    // Перенаправляем на Keycloak для logout
    const logoutUrl = `${this.keycloakUrl}/realms/${this.realm}/protocol/saml/logout?redirect_uri=${encodeURIComponent(this.redirectUri)}`;
    window.location.href = logoutUrl;
  }

  /**
   * Проверяет, есть ли у пользователя определенная роль
   */
  public hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user ? user.roles.includes(role) : false;
  }

  /**
   * Проверяет, является ли пользователь администратором
   */
  public isAdmin(): boolean {
    return this.hasRole('admin') || this.hasRole('administrator');
  }
}

// Создаем экземпляр сервиса
export const samlService = new SamlService();
