describe('Sessions Form E2E', () => {

  beforeEach(() => {
    // Mocks nécessaires
    cy.intercept('GET', '/api/session', []).as('sessionsList');
    cy.intercept('GET', '/api/teacher', [
      { id: 1, lastName: 'Dupont', firstName: 'Marie', createdAt: '2026-01-01', updatedAt: '2026-01-01' },
      { id: 2, lastName: 'Martin', firstName: 'Paul', createdAt: '2026-01-01', updatedAt: '2026-01-01' }
    ]).as('teachersList');

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

    // Login
    cy.visit('/login');
    cy.get('input[formControlName="email"]').type('yoga@studio.com');
    cy.get('input[formControlName="password"]').type('test!1234');
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/sessions');
  });

  it('Form Create - devrait afficher le formulaire de création', () => {
    cy.contains('button', 'Create').click();
    cy.url().should('include', '/sessions/create');
    cy.contains('Create session').should('be.visible');
    cy.get('input[formControlName="name"]').should('be.visible');
    cy.get('input[formControlName="date"]').should('be.visible');
    cy.get('mat-select[formControlName="teacher_id"]').should('be.visible');
    cy.get('textarea[formControlName="description"]').should('be.visible');
  });

  it('Form Create - devrait créer une nouvelle session avec succès', () => {
    cy.intercept('POST', '/api/session', {
      body: {
        id: 3,
        name: 'Nouvelle Session',
        date: '2026-03-10',
        description: 'Description de la nouvelle session',
        teacher_id: 1,
        users: []
      }
    }).as('createSession');

    cy.contains('button', 'Create').click();

    cy.get('input[formControlName="name"]').type('Nouvelle Session');
    cy.get('input[formControlName="date"]').type('2026-03-10');
    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').contains('Marie Dupont').click();
    cy.get('textarea[formControlName="description"]').type('Description de la nouvelle session');

    cy.get('button[type="submit"]').should('not.be.disabled').click();
    cy.url().should('include', '/sessions');
  });

  it('Form Create - devrait désactiver le bouton submit si le formulaire est invalide', () => {
    cy.contains('button', 'Create').click();
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="name"]').type('Test');
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="date"]').type('2026-03-10');
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('Form - devrait revenir à la liste des sessions avec le bouton retour', () => {
    cy.contains('button', 'Create').click();
    cy.get('button[mat-icon-button]').click();
    cy.url().should('include', '/sessions');
  });

  it('Form Create - devrait valider tous les champs requis', () => {
    cy.contains('button', 'Create').click();

    // Tester que tous les champs sont requis
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="name"]').type('Test Session');
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="date"]').type('2026-03-10');
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').first().click();
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('textarea[formControlName="description"]').type('Description complète');
    cy.get('button[type="submit"]').should('not.be.disabled');
  });

  it('Form Create - devrait afficher la liste des professeurs', () => {
    cy.contains('button', 'Create').click();

    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').should('have.length', 2);
    cy.get('mat-option').contains('Marie Dupont').should('be.visible');
    cy.get('mat-option').contains('Paul Martin').should('be.visible');
  });
});

