describe('Sessions List E2E', () => {
  beforeEach(() => {
    // Mock de la liste des sessions - doit être défini AVANT la visite
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [
        {
          id: 1,
          name: 'Session de Yoga',
          date: '2026-02-15',
          description: 'Une session relaxante de yoga pour débutants',
          teacher_id: 1,
          users: [],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-01'
        },
        {
          id: 2,
          name: 'Session avancée',
          date: '2026-02-20',
          description: 'Session pour pratiquants avancés',
          teacher_id: 2,
          users: [],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-01'
        }
      ]
    }).as('sessionsList');

    // Mock de la connexion utilisateur
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

    // Login et navigation vers les sessions
    cy.visit('/login');
    cy.get('input[formControlName="email"]').type('yoga@studio.com');
    cy.get('input[formControlName="password"]').type('test!1234');
    cy.get('button[type="submit"]').click();

    // Attendre la redirection vers /sessions
    cy.url().should('include', '/sessions');
  });

  it('Sessions List - devrait afficher la liste des sessions', () => {
    cy.contains('Rentals available').should('be.visible');
    cy.contains('Session de Yoga').should('be.visible');
    cy.contains('Session avancée').should('be.visible');
  });

  it('Sessions List - devrait afficher le bouton Create pour un admin', () => {
    cy.contains('button', 'Create').should('be.visible');
  });

  it('Sessions List - devrait afficher les détails des sessions', () => {
    cy.contains('Session de Yoga').should('be.visible');
    cy.contains('February 15, 2026').should('be.visible');
    cy.contains('Une session relaxante de yoga pour débutants').should('be.visible');
  });

  it('Sessions List - devrait naviguer vers le détail d\'une session', () => {
    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'Session de Yoga',
        date: '2026-02-15',
        description: 'Une session relaxante de yoga pour débutants',
        teacher_id: 1,
        users: [],
        createdAt: '2026-01-01',
        updatedAt: '2026-01-01'
      }
    }).as('sessionDetail');

    cy.intercept('GET', '/api/teacher/1', {
      statusCode: 200,
      body: {
        id: 1,
        lastName: 'Dupont',
        firstName: 'Marie',
        createdAt: '2026-01-01',
        updatedAt: '2026-01-01'
      }
    }).as('teacherDetail');

    cy.contains('Session de Yoga')
      .parent()
      .parent()
      .parent()
      .within(() => {
        cy.contains('button', 'Detail').click();
      });

    cy.url().should('include', '/sessions/detail/1');
  });

  it('Sessions List - devrait naviguer vers le formulaire de création', () => {
    cy.intercept('GET', '/api/teacher', [
      { id: 1, lastName: 'Dupont', firstName: 'Marie', createdAt: '2026-01-01', updatedAt: '2026-01-01' }
    ]).as('teachersList');

    cy.contains('button', 'Create').click();
    cy.url().should('include', '/sessions/create');
  });

  it('Sessions List - devrait afficher "Rentals available"', () => {
    cy.contains('Rentals available').should('be.visible');
  });
});

describe('Sessions List E2E - Non Admin', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [
        {
          id: 1,
          name: 'Session de Yoga',
          date: '2026-02-15',
          description: 'Une session relaxante de yoga pour débutants',
          teacher_id: 1,
          users: [],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-01'
        }
      ]
    }).as('sessionsList');

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

    cy.visit('/login');
    cy.get('input[formControlName="email"]').type('user@test.com');
    cy.get('input[formControlName="password"]').type('test!1234');
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/sessions');
  });

  it('Sessions List - ne devrait pas afficher le bouton Create pour un utilisateur non-admin', () => {
    cy.contains('button', 'Create').should('not.exist');
  });

  it('Sessions List - devrait pouvoir voir les sessions disponibles', () => {
    cy.contains('Session de Yoga').should('be.visible');
    cy.contains('February 15, 2026').should('be.visible');
  });

  it('Sessions List - devrait pouvoir naviguer vers le détail d\'une session', () => {
    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'Session de Yoga',
        date: '2026-02-15',
        description: 'Une session relaxante de yoga pour débutants',
        teacher_id: 1,
        users: [],
        createdAt: '2026-01-01',
        updatedAt: '2026-01-01'
      }
    }).as('sessionDetail');

    cy.intercept('GET', '/api/teacher/1', {
      statusCode: 200,
      body: {
        id: 1,
        lastName: 'Dupont',
        firstName: 'Marie',
        createdAt: '2026-01-01',
        updatedAt: '2026-01-01'
      }
    }).as('teacherDetail');

    cy.contains('Session de Yoga')
      .parent()
      .parent()
      .parent()
      .within(() => {
        cy.contains('button', 'Detail').click();
      });

    cy.url().should('include', '/sessions/detail/1');
  });
});
