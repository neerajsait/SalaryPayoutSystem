<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - SalaryCore Enterprise</title>
    <style>
        :root {
            --primary: #0052cc;
            --primary-hover: #0047b3;
            --secondary: #00875a;
            --bg-color: #f4f5f7;
            --card-bg: #ffffff;
            --text-main: #172b4d;
            --text-muted: #5e6c84;
            --border-color: #dfe1e6;
            --danger: #de350b;
        }
        * { margin: 0; padding: 0; box-sizing: border-box; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; }
        body { background-color: var(--bg-color); color: var(--text-main); min-height: 100vh; display: flex; flex-direction: column; }
        nav { background-color: #172b4d; border-bottom: 2px solid #091e42; padding: 0.75rem 2rem; display: flex; justify-content: space-between; align-items: center; position: sticky; top: 0; }
        .logo { font-size: 1.25rem; font-weight: bold; color: #ffffff; letter-spacing: 0.5px; }
        .container { max-width: 400px; margin: 4rem auto; padding: 0 1rem; width: 100%; }
        .card { background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 2px; padding: 1.5rem; }
        h1 { font-size: 2rem; margin-bottom: 1.5rem; border-bottom: 1px solid var(--border-color); padding-bottom: 0.5rem; }
        .form-group { margin-bottom: 1rem; }
        .form-group label { display: block; margin-bottom: 0.25rem; font-weight: bold; font-size: 0.85rem; }
        input { width: 100%; padding: 0.5rem; border: 1px solid var(--border-color); border-radius: 2px; font-size: 0.9rem; }
        input:focus { outline: none; border-color: var(--primary); }
        .btn { padding: 0.5rem 1rem; border: 1px solid transparent; border-radius: 2px; font-weight: bold; cursor: pointer; text-align: center; }
        .btn-primary { background-color: var(--primary); color: #ffffff; }
        .btn-primary:hover { background-color: var(--primary-hover); }
    </style>
</head>
<body>
    <nav>
        <div class="logo">SalaryCore Enterprise</div>
    </nav>
    <div class="container">
        <h1>System Authentication</h1>
        <div class="card">
            <% if (request.getParameter("error") != null) { %>
                <div style="color: var(--danger); font-weight: bold; margin-bottom: 1rem;">Invalid credentials. Access Denied.</div>
            <% } %>
            <% if (request.getParameter("logout") != null) { %>
                <div style="color: var(--secondary); font-weight: bold; margin-bottom: 1rem;">Securely logged out.</div>
            <% } %>
            <form action="/login" method="post">
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" required>
                </div>
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%;">Authenticate</button>
            </form>
            <div style="margin-top: 2rem; font-size: 0.85rem; color: var(--text-muted); border-top: 1px solid var(--border-color); padding-top: 1rem;">
                <p><strong>Demo Roles:</strong></p>
                <p>Admin: admin / admin123</p>
                <p>HR: hr / hr123</p>
            </div>
        </div>
    </div>
</body>
</html>
