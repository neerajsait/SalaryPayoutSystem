<%@ include file="header.jsp" %>

<div class="grid-2">
    <div>
        <h1 class="page-title">Employees</h1>
        <div class="card">
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Salary (Rs.)</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="employee-table-body">
                        <tr><td colspan="4" style="text-align:center;">Loading...</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <div>
        <h2 class="page-title" id="form-title" style="font-size: 2rem;">Add Employee</h2>
        <div class="card">
            <form id="add-employee-form" onsubmit="addEmployee(event)">
                <div class="form-group">
                    <label>Full Name</label>
                    <input type="text" id="name" required placeholder="John Doe">
                </div>
                <div class="form-group">
                    <label>Email Address</label>
                    <input type="email" id="email" required placeholder="john@example.com">
                </div>
                <div class="form-group">
                    <label>Phone Number</label>
                    <input type="text" id="phone" required placeholder="9876543210">
                </div>
                <div class="form-group">
                    <label>Bank Account UPI</label>
                    <input type="text" id="upi" required placeholder="john@upi">
                </div>
                <div class="form-group">
                    <label>Basic Salary (Rs.)</label>
                    <input type="number" id="salary" required placeholder="50000">
                </div>
                <button type="submit" id="submit-btn" class="btn btn-primary" style="width: 100%;">Add Employee</button>
                <button type="button" id="cancel-btn" class="btn btn-secondary" style="width: 100%; margin-top: 0.5rem; display: none;" onclick="resetForm()">Cancel Edit</button>
            </form>
        </div>
    </div>
</div>

<script>
    const EMPLOYEE_API = 'http://localhost:8081/api/employees';
    let editingId = null;
    let employeesData = [];

    async function fetchEmployees() {
        try {
            const res = await fetch(EMPLOYEE_API);
            employeesData = await res.json();
            const tbody = document.getElementById('employee-table-body');
            tbody.innerHTML = '';
            
            if(employeesData.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">No employees found</td></tr>';
                return;
            }

            employeesData.forEach(emp => {
                const badgeClass = emp.status === 'ACTIVE' ? 'badge badge-success' : 'badge badge-warning';
                tbody.innerHTML += `
                    <tr>
                        <td>#\${emp.id}</td>
                        <td><strong>\${emp.name}</strong></td>
                        <td>\${emp.email}</td>
                        <td>Rs. \${emp.basicSalary}</td>
                        <td><span class="\${badgeClass}">\${emp.status || 'ACTIVE'}</span></td>
                        <td>
                            <button class="btn btn-primary" style="padding: 0.2rem 0.5rem; font-size: 0.8rem;" onclick="editEmployee(\${emp.id})">Edit</button>
                            <button class="btn" style="padding: 0.2rem 0.5rem; font-size: 0.8rem; background-color: var(--danger); color: white;" onclick="deleteEmployee(\${emp.id})">Delete</button>
                        </td>
                    </tr>
                `;
            });
        } catch (e) {
            document.getElementById('employee-table-body').innerHTML = '<tr><td colspan="6" style="text-align:center;color:red;">Error loading employees. Ensure EmployeeService (8081) is running.</td></tr>';
            console.error(e);
        }
    }

    async function addEmployee(e) {
        e.preventDefault();
        const payload = {
            name: document.getElementById('name').value,
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value,
            bankAccountUpi: document.getElementById('upi').value,
            basicSalary: document.getElementById('salary').value,
            status: 'ACTIVE'
        };

        try {
            let res;
            if (editingId) {
                res = await fetch(`${EMPLOYEE_API}/${editingId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
            } else {
                res = await fetch(EMPLOYEE_API, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
            }
            
            if(res.ok) {
                showToast(editingId ? 'Employee updated successfully!' : 'Employee added successfully!', 'success');
                resetForm();
                fetchEmployees();
            } else {
                const text = await res.text();
                showToast('Failed to save employee: ' + text, 'error');
            }
        } catch(err) {
            showToast('Network error', 'error');
        }
    }

    function editEmployee(id) {
        const emp = employeesData.find(e => e.id === id);
        if(!emp) return;
        editingId = id;
        document.getElementById('form-title').innerText = 'Edit Employee';
        document.getElementById('submit-btn').innerText = 'Update Employee';
        document.getElementById('cancel-btn').style.display = 'block';
        
        document.getElementById('name').value = emp.name;
        document.getElementById('email').value = emp.email;
        document.getElementById('phone').value = emp.phone;
        document.getElementById('upi').value = emp.bankAccountUpi;
        document.getElementById('salary').value = emp.basicSalary;
    }
    
    function resetForm() {
        editingId = null;
        document.getElementById('add-employee-form').reset();
        document.getElementById('form-title').innerText = 'Add Employee';
        document.getElementById('submit-btn').innerText = 'Add Employee';
        document.getElementById('cancel-btn').style.display = 'none';
    }

    async function deleteEmployee(id) {
        if(!confirm("Are you sure you want to delete this employee?")) return;
        try {
            const res = await fetch(`${EMPLOYEE_API}/${id}`, { method: 'DELETE' });
            if(res.ok) {
                showToast('Employee deleted', 'success');
                fetchEmployees();
            } else {
                showToast('Failed to delete', 'error');
            }
        } catch(e) {
            showToast('Network error', 'error');
        }
    }

    // Init
    fetchEmployees();
</script>

<%@ include file="footer.jsp" %>
