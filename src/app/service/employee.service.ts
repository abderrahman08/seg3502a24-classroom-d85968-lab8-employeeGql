import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { Employee } from '../model/employee';
import { Apollo } from 'apollo-angular';
import { gql } from '@apollo/client/core';

const GET_EMPLOYEES = gql`
  query {
    employees {
      id
      name
      dateOfBirth
      city
      salary
      gender
      email
    }
  }
`;

const ADD_EMPLOYEE = gql`
  mutation addEmployee($input: CreateEmployeeInput!) {
    addEmployee(input: $input) {
      id
      name
      dateOfBirth
      city
      salary
      gender
      email
    }
  }
`;

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  employees$: BehaviorSubject<readonly Employee[]> = new BehaviorSubject<readonly Employee[]>([]);

  constructor(private apollo: Apollo) {
    // Fetch employees and update the BehaviorSubject
    this.apollo.watchQuery<any>({ query: GET_EMPLOYEES }).valueChanges.pipe(
      map(({ data }) => {
        this.employees$.next(data.employees);
      })
    ).subscribe();
  }

  // Observable getter for employees
  get $(): Observable<readonly Employee[]> {
    return this.employees$.asObservable();
  }

  // Add a new employee and update the BehaviorSubject
  addEmployee(employee: Employee) {
    const input = {
      name: employee.name,
      dateOfBirth: employee.dateOfBirth,
      city: employee.city,
      salary: employee.salary,
      gender: employee.gender,
      email: employee.email
    };

    return this.apollo.mutate<any>({
      mutation: ADD_EMPLOYEE,
      variables: { input }
    }).pipe(
      map(({ data }) => {
        this.employees$.next([...this.employees$.getValue(), data.addEmployee]);
      })
    );
  }
}
