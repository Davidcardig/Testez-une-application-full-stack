    cy.get('input[formControlName="password"]').type('pass123');
    // Tester lastName requis
    // Réinitialiser
    cy.get('input[formControlName="email"]').clear();
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="password"]').type('pass123');
    cy.get('input[formControlName="lastName"]').clear();
    cy.get('input[formControlName="password"]').clear();

    // Tester firstName requis
describe('Register E2E', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('Register - devrait afficher la page d\'inscription', () => {
    cy.url().should('include', '/register');
    cy.get('input[formControlName="email"]').should('be.visible');
    cy.get('input[formControlName="firstName"]').should('be.visible');
    cy.get('input[formControlName="lastName"]').should('be.visible');
    cy.get('input[formControlName="password"]').should('be.visible');
    cy.get('button[type="submit"]').should('be.visible');
  });

  it('Register - devrait créer un compte avec succès', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {
        message: 'User registered successfully!'
      }
    }).as('registerRequest');

    cy.get('input[formControlName="email"]').type('newuser@test.com');
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();

    cy.wait('@registerRequest');
    cy.url().should('include', '/login');
  });

  it('Register - devrait afficher une erreur si l\'email existe déjà', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: {
        message: 'Error: Email is already taken!'
      }
    }).as('registerRequestFailed');

    cy.get('input[formControlName="email"]').type('existing@test.com');
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();

    cy.wait('@registerRequestFailed');
    cy.get('.error').should('be.visible');
  });

  it('Register - devrait désactiver le bouton si le formulaire est invalide', () => {
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="email"]').type('test@test.com');
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="password"]').type('pass123');
    cy.get('button[type="submit"]').should('not.be.disabled');
  });

  it('Register - devrait valider le format email', () => {
    cy.get('input[formControlName="email"]').type('invalid-email');
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="password"]').type('pass123');
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('Register - devrait naviguer vers la page de login', () => {
    cy.contains('Login').click();
    cy.url().should('include', '/login');
  });

  it('Register - devrait gérer les erreurs réseau', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 500,
      body: {
        message: 'Internal Server Error'
      }
    }).as('registerError');

    cy.get('input[formControlName="email"]').type('test@test.com');
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();

    cy.wait('@registerError');
    cy.get('.error').should('be.visible');
  });

  it('Register - devrait vérifier que chaque champ est requis', () => {
    // Tester email requis
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="password"]').type('pass123');
    cy.get('button[type="submit"]').should('be.disabled');

    // Réinitialiser
    cy.get('input[formControlName="firstName"]').clear();
    cy.get('input[formControlName="lastName"]').clear();
    cy.get('input[formControlName="password"]').clear();

    // Tester firstName requis
    cy.get('input[formControlName="email"]').type('test@test.com');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="password"]').type('pass123');
    cy.get('button[type="submit"]').should('be.disabled');

    // Réinitialiser
    cy.get('input[formControlName="email"]').clear();
    cy.get('input[formControlName="lastName"]').clear();
    cy.get('input[formControlName="password"]').clear();

    // Tester lastName requis
    cy.get('input[formControlName="email"]').type('test@test.com');
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="password"]').type('pass123');
    cy.get('button[type="submit"]').should('be.disabled');
  });
});
