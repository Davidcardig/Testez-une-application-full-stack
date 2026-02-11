describe('Sessions Detail E2E', () => {

  describe('Détail de session - Admin', () => {
    beforeEach(() => {
      // Mocks nécessaires
      cy.intercept('GET', '/api/session', [
        { id: 1, name: 'Yoga session', date: '2026-02-15', description: 'Une session de yoga', teacher_id: 1, users: [2, 3] }
      ]).as('sessionsList');

      cy.intercept('GET', '/api/session/1', {
        body: {
          id: 1,
          name: 'Yoga session',
          date: '2026-02-15',
          description: 'Une session de yoga pour tous les niveaux',
          teacher_id: 1,
          users: [2, 3],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-15'
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

      // Login en tant qu'admin
      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('yoga@studio.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();
      cy.url().should('include', '/sessions');
    });

    it('Detail - devrait afficher les détails de la session', () => {
      cy.contains('Yoga session').parent().parent().parent().within(() => {
        cy.contains('button', 'Detail').click();
      });

      cy.url().should('include', '/sessions/detail/1');
      cy.contains('Yoga Session').should('be.visible');
      cy.contains('Marie DUPONT').should('be.visible');
      cy.contains('2 attendees').should('be.visible');
      cy.contains('February 15, 2026').should('be.visible');
      cy.contains('Une session de yoga pour tous les niveaux').should('be.visible');
    });

    it('Detail - devrait afficher le bouton Delete pour un admin', () => {
      cy.contains('Yoga session').parent().parent().parent().within(() => {
        cy.contains('button', 'Detail').click();
      });

      cy.contains('button', 'Delete').should('be.visible');
    });

    it('Detail - devrait supprimer une session en tant qu\'admin', () => {
      cy.intercept('DELETE', '/api/session/1', {
        statusCode: 200
      }).as('deleteSession');

      cy.contains('Yoga session').parent().parent().parent().within(() => {
        cy.contains('button', 'Detail').click();
      });

      cy.contains('button', 'Delete').click();
      cy.url().should('include', '/sessions');
    });

    it('Detail - devrait retourner à la liste avec le bouton retour', () => {
      cy.contains('Yoga session').parent().parent().parent().within(() => {
        cy.contains('button', 'Detail').click();
      });

      cy.get('button[mat-icon-button]').click();
      cy.url().should('include', '/sessions');
    });
  });

  describe('Détail de session - Utilisateur non-participant', () => {
    beforeEach(() => {
      // Mocks nécessaires
      cy.intercept('GET', '/api/session', [
        { id: 1, name: 'Yoga session', date: '2026-02-15', description: 'Une session de yoga', teacher_id: 1, users: [3, 4] }
      ]).as('sessionsList');

      cy.intercept('GET', '/api/session/1', {
        body: {
          id: 1,
          name: 'Yoga session',
          date: '2026-02-15',
          description: 'Une session de yoga pour tous les niveaux',
          teacher_id: 1,
          users: [3, 4],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-15'
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

      // Login en tant qu'utilisateur
      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('user@test.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();
      cy.url().should('include', '/sessions');
    });

    it('Detail - devrait afficher le bouton Participate pour un utilisateur non-participant', () => {
      cy.contains('Yoga session').parent().parent().parent().within(() => {
        cy.contains('button', 'Detail').click();
      });

      cy.contains('button', 'Participate').should('be.visible');
      cy.contains('button', 'Delete').should('not.exist');
    });

    it('Detail - devrait permettre à un utilisateur de participer', () => {
      cy.intercept('POST', '/api/session/1/participate/2', {
        statusCode: 200
      }).as('participate');

      cy.contains('Yoga session').parent().parent().parent().within(() => {
        cy.contains('button', 'Detail').click();
      });

      cy.contains('2 attendees').should('be.visible');

      cy.intercept('GET', '/api/session/1', {
        body: {
          id: 1,
          name: 'Yoga session',
          date: '2026-02-15',
          description: 'Une session de yoga pour tous les niveaux',
          teacher_id: 1,
          users: [2, 3, 4],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-15'
        }
      }).as('sessionDetailUpdated');

      cy.contains('button', 'Participate').click();
      cy.contains('3 attendees').should('be.visible');
      cy.contains('button', 'Do not participate').should('be.visible');
    });
  });

  describe('Détail de session - Utilisateur participant', () => {
    beforeEach(() => {
      // Mocks nécessaires
      cy.intercept('GET', '/api/session', [
        { id: 1, name: 'Yoga session', date: '2026-02-15', description: 'Une session de yoga', teacher_id: 1, users: [2, 3] }
      ]).as('sessionsList');

      cy.intercept('GET', '/api/session/1', {
        body: {
          id: 1,
          name: 'Yoga session',
          date: '2026-02-15',
          description: 'Une session de yoga pour tous les niveaux',
          teacher_id: 1,
          users: [2, 3],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-15'
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

      // Login en tant qu'utilisateur participant
      cy.visit('/login');
      cy.get('input[formControlName="email"]').type('user@test.com');
      cy.get('input[formControlName="password"]').type('test!1234');
      cy.get('button[type="submit"]').click();
      cy.url().should('include', '/sessions');
    });

    it('Detail - devrait afficher le bouton Do not participate pour un utilisateur participant', () => {
      cy.contains('Yoga session').parent().parent().parent().within(() => {
        cy.contains('button', 'Detail').click();
      });

      cy.contains('button', 'Do not participate').should('be.visible');
    });

    it('Detail - devrait permettre à un utilisateur de se désinscrire', () => {
      cy.intercept('DELETE', '/api/session/1/participate/2', {
        statusCode: 200
      }).as('unParticipate');

      cy.contains('Yoga session').parent().parent().parent().within(() => {
        cy.contains('button', 'Detail').click();
      });

      cy.contains('2 attendees').should('be.visible');

      cy.intercept('GET', '/api/session/1', {
        body: {
          id: 1,
          name: 'Yoga session',
          date: '2026-02-15',
          description: 'Une session de yoga pour tous les niveaux',
          teacher_id: 1,
          users: [3],
          createdAt: '2026-01-01',
          updatedAt: '2026-01-15'
        }
      }).as('sessionDetailUpdated');

      cy.contains('button', 'Do not participate').click();
      cy.contains('1 attendees').should('be.visible');
      cy.contains('button', 'Participate').should('be.visible');
    });
  });
});
