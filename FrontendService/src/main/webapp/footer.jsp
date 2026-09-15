    </div>
    
    <footer style="margin-top: 4rem; padding-top: 2rem; border-top: 1px solid var(--border-color); font-size: 0.85rem; color: var(--text-muted); display: flex; justify-content: space-between;">
        <div>&copy; 2026 SalaryCore Enterprise Systems. All rights reserved.</div>
        <div style="display: flex; gap: 1rem;">
            <a href="#" style="color: var(--primary); text-decoration: none;">Terms of Service</a>
            <a href="#" style="color: var(--primary); text-decoration: none;">Privacy Policy</a>
        </div>
    </footer>
    
    <div id="toast" class="toast">
        <span id="toast-icon"></span>
        <span id="toast-msg">Notification message</span>
    </div>

    <script>
        // Set active nav link
        const path = window.location.pathname;
        if(path === '/') document.getElementById('nav-home').classList.add('active');
        if(path.includes('/employees')) document.getElementById('nav-employees').classList.add('active');
        if(path.includes('/salaries')) document.getElementById('nav-salaries').classList.add('active');
        if(path.includes('/payments')) document.getElementById('nav-payments').classList.add('active');
        
        function showToast(message, type="success") {
            const toast = document.getElementById('toast');
            document.getElementById('toast-msg').innerText = message;
            toast.className = 'toast show ' + type;
            document.getElementById('toast-icon').innerText = type === 'error' ? '[ERROR]' : '[OK]';
            setTimeout(() => { toast.classList.remove('show'); }, 3000);
        }
    </script>
</body>
</html>
