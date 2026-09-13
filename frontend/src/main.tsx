import { StrictMode, type ReactNode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { AuthProvider, MockAuthProvider } from '@helex/core';
import {
  createHelexStore,
  HelexI18nBridge,
  HelexQueryProvider,
  AuthReduxBridge,
  GlobalApiErrorBridge,
} from '@helex/state';
import { AppRoot, HelexThemeProvider } from '@helex/ui';
import { App } from './App';

// The standard Helex provider stack, exactly as the production applications
// nest it (compare helex-tx modules/tedy/frontend/src/main.tsx). You should
// not need to change this file — your pages go under src/pages.
const store = createHelexStore();

/**
 * Dev auth: auto-sign-in a mock user, no login screen, no Keycloak. The
 * MockAuthProvider sends `Authorization: Bearer <username>` on every request —
 * the backend's MockBearerAuthFilter (auth.mock.enabled=true) accepts it.
 * Production builds use the real AuthProvider instead.
 */
const AuthGate = ({ children }: { children: ReactNode }) =>
  import.meta.env.DEV ? (
    <MockAuthProvider autoSignIn defaultUser="superadmin">
      {children}
    </MockAuthProvider>
  ) : (
    <AuthProvider>{children}</AuthProvider>
  );

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>
      <HelexI18nBridge>
        <HelexQueryProvider>
          <AuthGate>
            <AuthReduxBridge />
            <BrowserRouter>
              <HelexThemeProvider>
                <AppRoot appName="Animals Register">
                  <GlobalApiErrorBridge />
                  <App />
                </AppRoot>
              </HelexThemeProvider>
            </BrowserRouter>
          </AuthGate>
        </HelexQueryProvider>
      </HelexI18nBridge>
    </Provider>
  </StrictMode>,
);
