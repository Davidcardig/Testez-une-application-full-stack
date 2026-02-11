describe('Not Found E2E', () => {

  it('404 - devrait afficher la page 404 pour une route inexistante', () => {
    cy.visit('/route-qui-nexiste-pas');
    cy.url().should('include', '/404');
    cy.contains('Page not found !').should('be.visible');
  });

  it('404 - devrait afficher la page 404 quand on accède à /404', () => {
    cy.visit('/404');
    cy.url().should('include', '/404');
    cy.contains('Page not found !').should('be.visible');
  });

  it('404 - devrait rediriger vers la page 404 pour des routes inexistantes après login', () => {
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

    cy.visit('/une-route-inexistante');
    cy.url().should('include', '/404');
  });
});
