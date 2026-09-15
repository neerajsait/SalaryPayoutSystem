<%@ include file="header.jsp" %>

<div class="grid-2">
    <div>
        <h1 class="page-title">Process Payments</h1>
        <div class="card">
            <p style="margin-bottom: 1rem; color: var(--text-muted);">
                Create a Stripe PaymentIntent for an unpaid salary record. Once created, the webhook will handle completion.
            </p>
            <form id="create-payment-form" onsubmit="createPayment(event)">
                <div class="form-group">
                    <label>Salary Record ID</label>
                    <input type="number" id="salaryRecordId" required placeholder="e.g. 1">
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%;">Create Stripe Payment</button>
            </form>
            
            <div id="payment-result" style="margin-top: 2rem; display: none; background: #fafbfc; padding: 1rem; border: 1px solid var(--border-color); border-radius: 2px;">
                <h3 style="color: var(--primary);">PaymentIntent Created!</h3>
                <p><strong>Transaction ID:</strong> <span id="r-order-id"></span></p>
                <p><strong>Amount:</strong> Rs. <span id="r-amount"></span></p>
                <p><strong>Status:</strong> <span class="badge badge-warning" id="r-status"></span></p>
            </div>
        </div>
    </div>
</div>

<script>
    const PAYMENT_API = 'http://localhost:8083/api/payments';

    async function createPayment(e) {
        e.preventDefault();
        const payload = {
            salaryRecordId: parseInt(document.getElementById('salaryRecordId').value)
        };

        try {
            const res = await fetch(PAYMENT_API + '/create-order', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if(res.ok) {
                const data = await res.json();
                showToast('Payment order created successfully!', 'success');
                
                // Show result box
                document.getElementById('payment-result').style.display = 'block';
                document.getElementById('r-order-id').innerText = data.transactionId;
                document.getElementById('r-amount').innerText = data.amount;
                document.getElementById('r-status').innerText = data.status;
                
            } else {
                const text = await res.text();
                showToast('Failed: ' + text, 'error');
            }
        } catch(err) {
            showToast('Network error. Ensure PaymentService (8083) is running.', 'error');
        }
    }
</script>

<%@ include file="footer.jsp" %>
