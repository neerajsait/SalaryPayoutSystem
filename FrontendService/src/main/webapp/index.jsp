<%@ include file="header.jsp" %>

<h1 class="page-title">Administrative Dashboard</h1>
<p style="margin-bottom: 2rem; color: var(--text-muted);">Select a module to continue.</p>

<div class="module-list">
    <a href="/employees" class="module-item">
        <h3>Employee Management</h3>
        <p class="text-muted">Maintain employee master records and compliance data.</p>
    </a>
    
    <a href="/salaries" class="module-item">
        <h3>Salary Processing</h3>
        <p class="text-muted">Execute monthly salary generation operations.</p>
    </a>
    
    <a href="/payments" class="module-item">
        <h3>Payment Gateway</h3>
        <p class="text-muted">Initiate fiat disbursements via Stripe integration.</p>
    </a>
</div>

<%@ include file="footer.jsp" %>
