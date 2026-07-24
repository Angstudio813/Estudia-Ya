import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const token = typeof localStorage !== 'undefined' ? localStorage.getItem('estudiaya_token') : null;
  const tokenType = typeof localStorage !== 'undefined' ? localStorage.getItem('estudiaya_token_type') : null;

  if (!token) {
    return next(request);
  }

  const authHeader = `${tokenType ?? 'Bearer'} ${token}`.trim();

  return next(
    request.clone({
      setHeaders: {
        Authorization: authHeader,
      },
    }),
  );
};