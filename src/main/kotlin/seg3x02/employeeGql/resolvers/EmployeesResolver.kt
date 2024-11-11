package seg3x02.employeeGql.resolvers

import seg3x02.employeeGql.entity.Employee
import seg3x02.employeeGql.repository.EmployeesRepository
import seg3x02.employeeGql.types.CreateEmployeeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class EmployeesResolver @Autowired constructor(
    private val employeesRepository: EmployeesRepository
) {

    @QueryMapping
    fun employees(): List<Employee> {
        return employeesRepository.findAll()
    }

    @QueryMapping
    fun employeeById(@Argument id: String): Employee? {
        return employeesRepository.findById(id).orElse(null)
    }

    @MutationMapping
    fun addEmployee(@Argument input: CreateEmployeeInput): Employee {
        val employee = Employee(
            name = input.name ?: "", // Provide default values if nullable fields are null
            dateOfBirth = input.dateOfBirth ?: "",
            city = input.city ?: "",
            salary = input.salary ?: 0.0f,
            gender = input.gender,
            email = input.email
        )
        return employeesRepository.save(employee)
    }

    @MutationMapping
    fun deleteEmployee(@Argument id: String): Boolean {
        return if (employeesRepository.existsById(id)) {
            employeesRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    @MutationMapping
    fun updateEmployee(@Argument id: String, @Argument input: CreateEmployeeInput): Employee? {
        return employeesRepository.findById(id).map { employee ->
            val updatedEmployee = employee.copy(
                name = input.name ?: employee.name,
                dateOfBirth = input.dateOfBirth ?: employee.dateOfBirth,
                city = input.city ?: employee.city,
                salary = input.salary ?: employee.salary,
                gender = input.gender ?: employee.gender,
                email = input.email ?: employee.email
            )
            employeesRepository.save(updatedEmployee)
        }.orElse(null)
    }
}
