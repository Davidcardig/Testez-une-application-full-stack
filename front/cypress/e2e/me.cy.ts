describe('Me (Account) E2E', () => {

  describe('Profil utilisateur - Non admin', () => {
    beforeEach(() => {
      // Mocks nécessaires
      cy.intercept('GET', '/api/session', []).as('sessionsList');

      cy.intercept('POST', '/api/auth/login', {
        body: {
          token: 'fake-jwt-token',
          type: 'Bearer',
          id: 2,
          username: 'user@test.com',
          firstName: 'User',
          lastName: 'Test',
          admin: false
        }
      }).as('login');

      cy.intercept('GET', '/api/user/2', {
        body: {
          id: 2,
          email: 'user@test.com',
          lastName: 'Test',
          firstName: 'User',
          admin: false,
          createdAt: '2026-01-01',
          updatedAt: '2026-01-15'
        }
      }).as('userInfo');

      // Login en tant qu'utilisateur
      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('user@test.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();
      cy.url().should('include', '/sessions');
    });

    it('Me - devrait afficher les informations du profil utilisateur', () => {
      cy.contains('Account').click();

      cy.url().should('include', '/me');
      cy.contains('User information').should('be.visible');
      cy.contains('Name: User TEST').should('be.visible');
      cy.contains('Email: user@test.com').should('be.visible');
      cy.contains('Create at:').should('be.visible');
      cy.contains('Last update:').should('be.visible');
    });

    it('Me - devrait afficher le bouton de suppression pour un utilisateur non-admin', () => {
      cy.contains('Account').click();

      cy.contains('Delete my account:').should('be.visible');
      cy.contains('button', 'Detail').should('be.visible');
      cy.contains('You are admin').should('not.exist');
    });

    it('Me - devrait supprimer le compte utilisateur', () => {
      cy.intercept('DELETE', '/api/user/2', {
        statusCode: 200
      }).as('deleteUser');

      cy.contains('Account').click();
      cy.contains('button', 'Detail').click();

      cy.contains('Your account has been deleted !').should('be.visible');
      cy.url().should('eq', 'http://localhost:4200/');
    });

    it('Me - devrait retourner en arrière avec le bouton retour', () => {
      cy.contains('Account').click();
      cy.get('button[mat-icon-button]').click();
      cy.url().should('include', '/sessions');
    });

    it('Me - devrait formater correctement le nom (prénom NOM)', () => {
      cy.contains('Account').click();
      cy.contains('Name: User TEST').should('be.visible');
    });
  });

  describe('Profil utilisateur - Admin', () => {
    beforeEach(() => {
      // Mocks nécessaires
      cy.intercept('GET', '/api/session', []).as('sessionsList');

      cy.intercept('POST', '/api/auth/login', {
        body: {
          token: 'fake-jwt-token',
          type: 'Bearer',
          id: 1,
          username: 'yoga@studio.com',
          firstName: 'Admin',
          lastName: 'Admin',
          admin: true
        }
      }).as('login');

      cy.intercept('GET', '/api/user/1', {
        body: {
          id: 1,
          email: 'yoga@studio.com',
          lastName: 'Admin',
          firstName: 'Admin',
          admin: true,
          createdAt: '2026-01-01',
          updatedAt: '2026-01-15'
        }
      }).as('userInfo');

      // Login en tant qu'admin
      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('yoga@studio.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();
      cy.url().should('include', '/sessions');
    });

    it('Me - devrait afficher "You are admin" pour un administrateur', () => {
      cy.contains('Account').click();

      cy.contains('You are admin').should('be.visible');
      cy.contains('Delete my account:').should('not.exist');
    });

    it('Me - ne devrait pas afficher le bouton de suppression pour un admin', () => {
      cy.contains('Account').click();

      cy.contains('button', 'Detail').should('not.exist');
    });
  });

  describe('Navigation', () => {
    beforeEach(() => {
      cy.intercept('GET', '/api/session', []).as('sessionsList');

      cy.intercept('POST', '/api/auth/login', {
        body: {
          token: 'fake-jwt-token',
          type: 'Bearer',
          id: 2,
          username: 'user@test.com',
          firstName: 'User',
          lastName: 'Test',
          admin: false
        }
      }).as('login');

      cy.intercept('GET', '/api/user/2', {
        body: {
          id: 2,
          email: 'user@test.com',
          lastName: 'Test',
          firstName: 'User',
          admin: false,
          createdAt: '2026-01-01',
          updatedAt: '2026-01-15'
        }
      }).as('userInfo');

      // Login
      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('user@test.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();
      cy.url().should('include', '/sessions');
    });

    it('Me - devrait se déconnecter via le bouton Logout', () => {
      cy.contains('Account').click();
      cy.get('button[mat-icon-button]').click();
      cy.url().should('include', '/sessions');

      // Test de déconnexion depuis n'importe quelle page
      cy.contains('Logout').click();
      cy.url().should('eq', 'http://localhost:4200/');
});
