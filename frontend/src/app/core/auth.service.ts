import { Injectable } from '@angular/core';

interface LoginSession {
  token: string;
  type: string;
}

export interface UserProfile {
  usuarioId: number;
  nombre: string;
  apellido: string;
  email: string;
  nivel: string;
  grado: number;
  xpTotal?: number;
  nivelJuego?: number;
  rachaActual?: number;
  rachaMasAlta?: number;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly tokenKey = 'estudiaya_token';
  private readonly tokenTypeKey = 'estudiaya_token_type';
  private readonly profileKey = 'estudiaya_user_profile';

  isLoggedIn(): boolean {
    return this.getToken().length > 0;
  }

  saveSession(session: LoginSession): void {
    if (typeof localStorage === 'undefined') {
      return;
    }

    localStorage.setItem(this.tokenKey, session.token);
    localStorage.setItem(this.tokenTypeKey, session.type);
  }

  saveEmail(email: string): void {
    this.saveProfile({
      usuarioId: 1,
      nombre: email,
      apellido: '',
      email,
      nivel: '',
      grado: 0,
    });
  }

  saveProfile(profile: UserProfile): void {
    if (typeof localStorage === 'undefined') {
      return;
    }

    localStorage.setItem(this.profileKey, JSON.stringify(profile));
  }

  clearSession(): void {
    if (typeof localStorage === 'undefined') {
      return;
    }

    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.tokenTypeKey);
    localStorage.removeItem(this.profileKey);
  }

  getToken(): string {
    if (typeof localStorage === 'undefined') {
      return '';
    }

    return localStorage.getItem(this.tokenKey) ?? '';
  }

  getAuthHeader(): string {
    const token = this.getToken();
    const type = this.getTokenType();

    return token ? `${type} ${token}` : '';
  }

  private getTokenType(): string {
    if (typeof localStorage === 'undefined') {
      return 'Bearer';
    }

    return localStorage.getItem(this.tokenTypeKey) ?? 'Bearer';
  }

  getDisplayName(): string {
    const profile = this.getProfile();

    if (!profile) {
      return 'Estudiante';
    }

    return [profile.nombre, profile.apellido].filter(Boolean).join(' ').trim() || profile.email || 'Estudiante';
  }

  getProfile(): UserProfile | null {
    if (typeof localStorage === 'undefined') {
      return null;
    }

    const rawProfile = localStorage.getItem(this.profileKey);
    if (!rawProfile) {
      return null;
    }

    try {
      return JSON.parse(rawProfile) as UserProfile;
    } catch {
      return null;
    }
  }

  getUserId(): number {
    return this.getProfile()?.usuarioId ?? 1;
  }
}
