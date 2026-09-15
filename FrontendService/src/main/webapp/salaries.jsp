<%@ include file="header.jsp" %>

<div class="grid-2">
    <div>
        <h1 class="page-title">Salary Records</h1>
        <div class="card">
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Emp ID</th>
                            <th>Month</th>
                            <th>Amount</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody id="salary-table-body">
                        <tr><td colspan="5" style="text-align:center;">Loading...</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <div>
        <h2 class="page-title" style="font-size: 2rem;">Generate Single Salary</h2>
        <div class="card" style="margin-bottom: 2rem;">
            <form id="generate-salary-form" onsubmit="generateSalary(event)">
                <div class="form-group">
                    <label>Employee ID</label>
                    <input type="number" id="empId" required placeholder="e.g. 1">
                </div>
                <div class="form-group">
                    <label>Month</label>
                    <select id="month" required>
                        <option value="January">January</option>
                        <option value="February">February</option>
                        <option value="March">March</option>
                        <option value="April">April</option>
                        <option value="May">May</option>
                        <option value="June">June</option>
                        <option value="July">July</option>
                        <option value="August">August</option>
                        <option value="September" selected>September</option>
                        <option value="October">October</option>
                        <option value="November">November</option>
                        <option value="December">December</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Year</label>
                    <input type="number" id="year" required value="2026">
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%;">Generate Salary</button>
            </form>
        </div>

        <h2 class="page-title" style="font-size: 2rem; color: var(--secondary);">Batch Process</h2>
        <div class="card">
            <p style="margin-bottom: 1rem; color: var(--text-muted);">Automatically generate salaries and trigger payments for all ACTIVE employees for a given month.</p>
            <form id="generate-all-form" onsubmit="generateAllSalaries(event)">
                <div class="form-group">
                    <label>Month</label>
                    <select id="all-month" required>
                        <option value="January">January</option>
                        <option value="February">February</option>
                        <option value="March">March</option>
                        <option value="April">April</option>
                        <option value="May">May</option>
                        <option value="June">June</option>
                        <option value="July">July</option>
                        <option value="August">August</option>
                        <option value="September" selected>September</option>
                        <option value="October">October</option>
                        <option value="November">November</option>
                        <option value="December">December</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Year</label>
                    <input type="number" id="all-year" required value="2026">
                </div>
                <button type="submit" class="btn" style="width: 100%; background-color: var(--secondary); color: white; font-weight: bold;">Generate & Pay All Active Employees</button>
            </form>
        </div>
    </div>
</div>

<script>
    const SALARY_API = 'http://localhost:8081/api/salaries';

    function getBadgeClass(status) {
        if(status === 'PAID') return 'badge badge-success';
        if(status === 'PENDING') return 'badge badge-warning';
        return 'badge';
    }

    async function fetchSalaries() {
        try {
            const res = await fetch(SALARY_API);
            const salaries = await res.json();
            const tbody = document.getElementById('salary-table-body');
            tbody.innerHTML = '';
            
            if(salaries.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">No salary records found</td></tr>';
                return;
            }

            salaries.forEach(sal => {
                tbody.innerHTML += `
                    <tr>
                        <td>#\${sal.id}</td>
                        <td>Emp #\${sal.employeeId}</td>
                        <td>\${sal.month} \${sal.year}</td>
                        <td>Rs. \${sal.amount}</td>
                        <td><span class="\${getBadgeClass(sal.status)}">\${sal.status}</span></td>
                    </tr>
                `;
            });
        } catch (e) {
            document.getElementById('salary-table-body').innerHTML = '<tr><td colspan="5" style="text-align:center;color:red;">Error loading salaries. Ensure EmployeeService (8081) is running.</td></tr>';
        }
    }

    async function generateSalary(e) {
        e.preventDefault();
        const payload = {
            employeeId: parseInt(document.getElementById('empId').value),
            month: document.getElementById('month').value,
            year: parseInt(document.getElementById('year').value)
        };

        try {
            const res = await fetch(SALARY_API + '/generate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if(res.ok) {
                showToast('Salary generated successfully!', 'success');
                document.getElementById('generate-salary-form').reset();
                fetchSalaries();
            } else {
                const text = await res.text();
                showToast('Failed: ' + text, 'error');
            }
        } catch(err) {
            showToast('Network error', 'error');
        }
    }

    async function generateAllSalaries(e) {
        e.preventDefault();
        const payload = {
            month: document.getElementById('all-month').value,
            year: parseInt(document.getElementById('all-year').value)
        };

        if(!confirm(`Are you sure you want to generate salaries and process payments for ALL active employees for ${payload.month} ${payload.year}?`)) {
            return;
        }

        try {
            const res = await fetch(SALARY_API + '/generate-all', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if(res.ok) {
                const records = await res.json();
                showToast(`Successfully processed ${records.length} salaries! Payments initiated.`, 'success');
                fetchSalaries();
            } else {
                const text = await res.text();
                showToast('Failed: ' + text, 'error');
            }
        } catch(err) {
            showToast('Network error', 'error');
        }
    }

    // Init
    fetchSalaries();
</script>

<%@ include file="footer.jsp" %>
