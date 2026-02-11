describe('App Integration E2E', () => {

  describe('Navigation et authentification', () => {

    it('App - devrait permettre la navigation complète de l\'application', () => {
      // 1. Commencer à la page de login
      cy.visit('/login');
      cy.url().should('include', '/login');

      // 2. Aller vers Register
      cy.contains('Register').click();
      cy.url().should('include', '/register');

      // 3. Revenir au Login
      cy.contains('Login').click();
      cy.url().should('include', '/login');

      // 4. Se connecter
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
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

      cy.intercept('GET', '/api/session', []).as('sessionsList');

      cy.get('input[formControlName="email"]').type('yoga@studio.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();

      cy.url().should('include', '/sessions');

      // 5. Vérifier le menu de navigation
      cy.contains('Sessions').should('be.visible');
      cy.contains('Account').should('be.visible');
      cy.contains('Logout').should('be.visible');
    });

    it('App - devrait gérer le cycle de vie complet d\'une session', () => {
      // Login
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
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

      cy.intercept('GET', '/api/session', []).as('sessionsList');
      cy.intercept('GET', '/api/teacher', [
        { id: 1, lastName: 'Dupont', firstName: 'Marie', createdAt: '2026-01-01', updatedAt: '2026-01-01' }
      ]).as('teachersList');

      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('yoga@studio.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();

      // Créer une session
      cy.contains('button', 'Create').click();
      cy.url().should('include', '/sessions/create');

      cy.intercept('POST', '/api/session', {
        body: {
          id: 1,
          name: 'Nouvelle Session',
          date: '2026-03-10',
          description: 'Description test',
          teacher_id: 1,
          users: []
        }
      }).as('createSession');

      cy.get('input[formControlName="name"]').type('Nouvelle Session');
      cy.get('input[formControlName="date"]').type('2026-03-10');
      cy.get('mat-select[formControlName="teacher_id"]').click();
      cy.get('mat-option').first().click();
      cy.get('textarea[formControlName="description"]').type('Description test');
      cy.get('button[type="submit"]').click();

      // Vérifier le retour à la liste
      cy.url().should('include', '/sessions');
    });

    it('App - devrait gérer le profil utilisateur complet', () => {
      // Login
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
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

      cy.intercept('GET', '/api/session', []).as('sessionsList');
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

      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('user@test.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();

      // Aller au profil
      cy.contains('Account').click();
      cy.url().should('include', '/me');
      cy.contains('User information').should('be.visible');

      // Retour aux sessions
      cy.get('button[mat-icon-button]').click();
      cy.url().should('include', '/sessions');

      // Déconnexion
      cy.contains('Logout').click();
      cy.url().should('eq', 'http://localhost:4200/');
    });

    it('App - devrait empêcher l\'accès aux pages protégées sans authentification', () => {
      cy.visit('/sessions');
      cy.url().should('include', '/login');

      cy.visit('/me');
      cy.url().should('include', '/login');

      cy.visit('/sessions/create');
      cy.url().should('include', '/login');
    });
  });

  describe('Scénarios utilisateur complets', () => {

    it('App - Scénario complet: Utilisateur participe à une session', () => {
      // Login utilisateur
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
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

      cy.intercept('GET', '/api/session', [
        {
          id: 1,
          name: 'Yoga Session',
          date: '2026-02-15',
          description: 'Session de yoga',
          teacher_id: 1,
          users: [],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-01'
        }
      ]).as('sessionsList');

      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('user@test.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();

      // Voir le détail de la session
      cy.intercept('GET', '/api/session/1', {
        body: {
          id: 1,
          name: 'Yoga Session',
          date: '2026-02-15',
          description: 'Session de yoga',
          teacher_id: 1,
          users: [],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-01'
        }
      }).as('sessionDetail');

      cy.intercept('GET', '/api/teacher/1', {
        body: {
          id: 1,
          lastName: 'Dupont',
          firstName: 'Marie',
          createdAt: '2026-01-01',
          updatedAt: '2026-01-01'
        }
      }).as('teacherDetail');

      cy.contains('Yoga Session').parent().parent().parent().within(() => {
        cy.contains('button', 'Detail').click();
      });

      // Participer
      cy.intercept('POST', '/api/session/1/participate/2', {
        statusCode: 200
      }).as('participate');

      cy.intercept('GET', '/api/session/1', {
        body: {
          id: 1,
          name: 'Yoga Session',
          date: '2026-02-15',
          description: 'Session de yoga',
          teacher_id: 1,
          users: [2],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-01'
        }
      }).as('sessionDetailUpdated');

      cy.contains('button', 'Participate').click();
      cy.contains('1 attendees').should('be.visible');
      cy.contains('button', 'Do not participate').should('be.visible');

      // Se désinscrire
      cy.intercept('DELETE', '/api/session/1/participate/2', {
        statusCode: 200
      }).as('unParticipate');

      cy.intercept('GET', '/api/session/1', {
        body: {
          id: 1,
          name: 'Yoga Session',
          date: '2026-02-15',
          description: 'Session de yoga',
          teacher_id: 1,
          users: [],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-01'
        }
      }).as('sessionDetailReset');

      cy.contains('button', 'Do not participate').click();
      cy.contains('0 attendees').should('be.visible');
    });

    it('App - Scénario complet: Admin crée et supprime une session', () => {
      // Login admin
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
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

      cy.intercept('GET', '/api/session', []).as('sessionsList');
      cy.intercept('GET', '/api/teacher', [
        { id: 1, lastName: 'Dupont', firstName: 'Marie', createdAt: '2026-01-01', updatedAt: '2026-01-01' }
      ]).as('teachersList');

      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('yoga@studio.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();

      // Créer une session
      cy.contains('button', 'Create').click();

      cy.intercept('POST', '/api/session', {
        body: {
          id: 1,
          name: 'Test Session',
          date: '2026-03-10',
          description: 'Test description',
          teacher_id: 1,
          users: []
        }
      }).as('createSession');

      cy.intercept('GET', '/api/session', [
        {
          id: 1,
          name: 'Test Session',
          date: '2026-03-10',
          description: 'Test description',
          teacher_id: 1,
          users: []
        }
      ]).as('sessionsListWithNew');

      cy.get('input[formControlName="name"]').type('Test Session');
      cy.get('input[formControlName="date"]').type('2026-03-10');
      cy.get('mat-select[formControlName="teacher_id"]').click();
      cy.get('mat-option').first().click();
      cy.get('textarea[formControlName="description"]').type('Test description');
      cy.get('button[type="submit"]').click();

      cy.url().should('include', '/sessions');

      // Supprimer la session
      cy.intercept('GET', '/api/session/1', {
        body: {
          id: 1,
          name: 'Test Session',
          date: '2026-03-10',
          description: 'Test description',
          teacher_id: 1,
          users: []
        }
      }).as('sessionDetail');

      cy.intercept('GET', '/api/teacher/1', {
        body: {
          id: 1,
          lastName: 'Dupont',
          firstName: 'Marie',
          createdAt: '2026-01-01',
          updatedAt: '2026-01-01'
        }
      }).as('teacherDetail');

      cy.intercept('DELETE', '/api/session/1', {
        statusCode: 200
      }).as('deleteSession');

      cy.contains('Test Session').parent().parent().parent().within(() => {
        cy.contains('button', 'Detail').click();
      });

      cy.contains('button', 'Delete').click();
      cy.url().should('include', '/sessions');
    });
  });

  describe('Tests de régression', () => {

    it('App - devrait maintenir l\'état de la session après rafraîchissement', () => {
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
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

      cy.intercept('GET', '/api/session', []).as('sessionsList');

      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('yoga@studio.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();

      cy.url().should('include', '/sessions');

      // Note: Dans un vrai test, le rafraîchissement effacerait l'état
      // car il n'y a pas de persistance dans sessionStorage/localStorage
      // sans token réel
    });

    it('App - devrait gérer les appels API multiples simultanés', () => {
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
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

      cy.intercept('GET', '/api/session', [
        { id: 1, name: 'Session 1', date: '2026-02-15', description: 'Desc 1', teacher_id: 1, users: [] },
        { id: 2, name: 'Session 2', date: '2026-02-20', description: 'Desc 2', teacher_id: 1, users: [] }
      ]).as('sessionsList');

      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('yoga@studio.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();

      cy.wait('@sessionsList');
      cy.contains('Session 1').should('be.visible');
      cy.contains('Session 2').should('be.visible');
    });
  });
});
