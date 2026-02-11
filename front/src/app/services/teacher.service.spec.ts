import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService]
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('devrait être créé', () => {
    expect(service).toBeTruthy();
  });

  describe('Tests unitaires', () => {
    describe('all()', () => {
      it('devrait retourner un Observable de tableau de Teacher', () => {
        const mockTeachers: Teacher[] = [
          {
            id: 1,
            lastName: 'Dupont',
            firstName: 'Jean',
            createdAt: new Date('2024-01-01'),
            updatedAt: new Date('2024-01-01')
          },
          {
            id: 2,
            lastName: 'Martin',
            firstName: 'Marie',
            createdAt: new Date('2024-01-02'),
            updatedAt: new Date('2024-01-02')
          }
        ];

        service.all().subscribe((teachers) => {
          expect(teachers).toEqual(mockTeachers);
          expect(teachers.length).toBe(2);
        });

        const req = httpMock.expectOne('api/teacher');
        expect(req.request.method).toBe('GET');
        req.flush(mockTeachers);
      });

      it('should return an empty array when no teachers exist', () => {
        service.all().subscribe((teachers) => {
          expect(teachers).toEqual([]);
          expect(teachers.length).toBe(0);
        });

        const req = httpMock.expectOne('api/teacher');
        expect(req.request.method).toBe('GET');
        req.flush([]);
      });

      it('should handle HTTP error for all()', () => {
        const errorMessage = 'Server error';

        service.all().subscribe(
          () => fail('should have failed with server error'),
          (error) => {
            expect(error.status).toBe(500);
            expect(error.statusText).toBe('Internal Server Error');
          }
        );

        const req = httpMock.expectOne('api/teacher');
        req.flush(errorMessage, { status: 500, statusText: 'Internal Server Error' });
      });

      it('should call the correct API endpoint for all()', () => {
        service.all().subscribe();

        const req = httpMock.expectOne('api/teacher');
        expect(req.request.url).toBe('api/teacher');
        req.flush([]);
      });
    });

    describe('detail()', () => {
      it('should return a single Teacher by id', () => {
        const mockTeacher: Teacher = {
          id: 1,
          lastName: 'Dupont',
          firstName: 'Jean',
          createdAt: new Date('2024-01-01'),
          updatedAt: new Date('2024-01-01')
        };

        service.detail('1').subscribe((teacher) => {
          expect(teacher).toEqual(mockTeacher);
          expect(teacher.id).toBe(1);
          expect(teacher.lastName).toBe('Dupont');
        });

        const req = httpMock.expectOne('api/teacher/1');
        expect(req.request.method).toBe('GET');
        req.flush(mockTeacher);
      });

      it('should call the correct API endpoint with provided id', () => {
        const teacherId = '42';
        const mockTeacher: Teacher = {
          id: 42,
          lastName: 'Test',
          firstName: 'Teacher',
          createdAt: new Date(),
          updatedAt: new Date()
        };

        service.detail(teacherId).subscribe();

        const req = httpMock.expectOne(`api/teacher/${teacherId}`);
        expect(req.request.url).toBe(`api/teacher/${teacherId}`);
        req.flush(mockTeacher);
      });

      it('should handle HTTP 404 error when teacher not found', () => {
        service.detail('999').subscribe(
          () => fail('should have failed with 404 error'),
          (error) => {
            expect(error.status).toBe(404);
            expect(error.statusText).toBe('Not Found');
          }
        );

        const req = httpMock.expectOne('api/teacher/999');
        req.flush('Teacher not found', { status: 404, statusText: 'Not Found' });
      });

      it('should handle different id formats (string numbers)', () => {
        const mockTeacher: Teacher = {
          id: 123,
          lastName: 'Smith',
          firstName: 'John',
          createdAt: new Date(),
          updatedAt: new Date()
        };

        service.detail('123').subscribe((teacher) => {
          expect(teacher.id).toBe(123);
        });

        const req = httpMock.expectOne('api/teacher/123');
        req.flush(mockTeacher);
      });
    });
  });

  describe('Tests d\'intégration', () => {
    it('devrait récupérer tous les professeurs puis obtenir le détail du premier professeur', (done) => {
      const mockTeachers: Teacher[] = [
        {
          id: 1,
          lastName: 'Dupont',
          firstName: 'Jean',
          createdAt: new Date('2024-01-01'),
          updatedAt: new Date('2024-01-01')
        },
        {
          id: 2,
          lastName: 'Martin',
          firstName: 'Marie',
          createdAt: new Date('2024-01-02'),
          updatedAt: new Date('2024-01-02')
        }
      ];

      const mockTeacherDetail: Teacher = {
        id: 1,
        lastName: 'Dupont',
        firstName: 'Jean',
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-01')
      };

      service.all().subscribe((teachers) => {
        expect(teachers.length).toBe(2);
        const firstTeacherId = teachers[0].id.toString();

        service.detail(firstTeacherId).subscribe((teacher) => {
          expect(teacher.id).toBe(mockTeacherDetail.id);
          expect(teacher.lastName).toBe(mockTeacherDetail.lastName);
          done();
        });

        const detailReq = httpMock.expectOne(`api/teacher/${firstTeacherId}`);
        detailReq.flush(mockTeacherDetail);
      });

      const allReq = httpMock.expectOne('api/teacher');
      allReq.flush(mockTeachers);
    });

    it('devrait gérer plusieurs requêtes consécutives à all()', () => {
      const mockTeachers: Teacher[] = [
        {
          id: 1,
          lastName: 'Teacher',
          firstName: 'One',
          createdAt: new Date(),
          updatedAt: new Date()
        }
      ];

      service.all().subscribe((teachers) => {
        expect(teachers.length).toBe(1);
      });

      service.all().subscribe((teachers) => {
        expect(teachers.length).toBe(1);
      });

      const requests = httpMock.match('api/teacher');
      expect(requests.length).toBe(2);
      requests.forEach(req => req.flush(mockTeachers));
    });

    it('devrait gérer plusieurs requêtes consécutives à detail() avec différents ids', () => {
      const mockTeacher1: Teacher = {
        id: 1,
        lastName: 'First',
        firstName: 'Teacher',
        createdAt: new Date(),
        updatedAt: new Date()
      };

      const mockTeacher2: Teacher = {
        id: 2,
        lastName: 'Second',
        firstName: 'Teacher',
        createdAt: new Date(),
        updatedAt: new Date()
      };

      service.detail('1').subscribe((teacher) => {
        expect(teacher.id).toBe(1);
      });

      service.detail('2').subscribe((teacher) => {
        expect(teacher.id).toBe(2);
      });

      const req1 = httpMock.expectOne('api/teacher/1');
      const req2 = httpMock.expectOne('api/teacher/2');

      req1.flush(mockTeacher1);
      req2.flush(mockTeacher2);
    });

    it('devrait maintenir une gestion appropriée des erreurs sur plusieurs requêtes', () => {
      service.all().subscribe(
        () => {},
        (error) => expect(error.status).toBe(500)
      );

      service.detail('1').subscribe(
        (teacher) => expect(teacher).toBeDefined()
      );

      const allReq = httpMock.expectOne('api/teacher');
      const detailReq = httpMock.expectOne('api/teacher/1');

      allReq.flush('Error', { status: 500, statusText: 'Server Error' });
      detailReq.flush({
        id: 1,
        lastName: 'Test',
        firstName: 'Teacher',
        createdAt: new Date(),
        updatedAt: new Date()
      });
    });
  });
});
