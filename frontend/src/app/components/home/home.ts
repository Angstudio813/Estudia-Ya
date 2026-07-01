import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private readonly authService = inject(AuthService);

  protected readonly nombreUsuario = computed(() => this.authService.getDisplayName());
  protected readonly saludo = computed(() => this.obtenerSaludo());

  private obtenerSaludo(): string {
    const hora = new Date().getHours();

    if (hora < 12) {
      return 'Buenos días';
    }

    if (hora < 19) {
      return 'Buenas tardes';
    }

    return 'Buenas noches';
  }
}
