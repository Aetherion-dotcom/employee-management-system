// ============================================
// WorkForceHub - Main JavaScript
// ============================================

const WorkForceHub = {
    API_BASE: '/api/v1',
    token: null,

    init() {
        this.token = this.getCookie('jwt');
        this.initTheme();
        this.initSidebar();
        this.initValidation();
        this.initTooltips();
    },

    // ===== Theme Toggle =====
    initTheme() {
        const saved = localStorage.getItem('wfh-theme') || 'light';
        document.documentElement.setAttribute('data-bs-theme', saved);
        const btn = document.getElementById('themeToggle');
        if (btn) {
            btn.innerHTML = saved === 'dark' ? '<i class="bi bi-sun"></i>' : '<i class="bi bi-moon"></i>';
            btn.addEventListener('click', () => this.toggleTheme());
        }
    },

    toggleTheme() {
        const current = document.documentElement.getAttribute('data-bs-theme');
        const next = current === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-bs-theme', next);
        localStorage.setItem('wfh-theme', next);
        const btn = document.getElementById('themeToggle');
        if (btn) btn.innerHTML = next === 'dark' ? '<i class="bi bi-sun"></i>' : '<i class="bi bi-moon"></i>';
    },

    // ===== Sidebar =====
    
    // ===== Sidebar =====
    initSidebar() {
        const toggle = document.getElementById('menuToggle');
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebarOverlay');
        
        if (toggle && sidebar) {
            toggle.addEventListener('click', () => {
                if (window.innerWidth < 992) {
                    sidebar.classList.toggle('show');
                    if (overlay) overlay.classList.toggle('show');
                } else {
                    sidebar.classList.toggle('collapsed');
                }
            });
        }
        
        const closeBtn = document.getElementById('sidebarCloseBtn');
        if (closeBtn && sidebar) {
            closeBtn.addEventListener('click', () => {
                if (window.innerWidth < 992) {
                    sidebar.classList.remove('show');
                    if (overlay) overlay.classList.remove('show');
                } else {
                    sidebar.classList.add('collapsed');
                }
            });
        }

        if (overlay) {
            overlay.addEventListener('click', () => {
                sidebar.classList.remove('show');
                overlay.classList.remove('show');
            });
        }

        // Active nav link
        const currentPath = window.location.pathname;
        document.querySelectorAll('.nav-link-custom').forEach(link => {
            if (link.getAttribute('href') === currentPath) link.classList.add('active');
        });
    },

    
    initValidation() {
        const forms = document.querySelectorAll('.needs-validation')
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault()
                    event.stopPropagation()
                }
                form.classList.add('was-validated')
            }, false)
        })
    },

    initTooltips() {
        const tooltipEls = document.querySelectorAll('[data-bs-toggle="tooltip"]');
        tooltipEls.forEach(el => new bootstrap.Tooltip(el));
    },

    // ===== API Helpers =====
    async apiCall(url, method = 'GET', body = null) {
        const headers = { 'Content-Type': 'application/json' };
        if (this.token) headers['Authorization'] = `Bearer ${this.token}`;
        const options = { method, headers, credentials: 'include' };
        if (body) options.body = JSON.stringify(body);
        const response = await fetch(this.API_BASE + url, options);
        if (response.status === 401) { window.location.href = '/login'; return; }
        return response.json();
    },

    // ===== Auth =====
    async login(usernameOrEmail, password) {
        try {
            const resp = await this.apiCall('/auth/login', 'POST', { usernameOrEmail, password });
            if (resp.success) {
                this.token = resp.data.accessToken;
                localStorage.setItem('wfh-refresh-token', resp.data.refreshToken);
                this.showToast('Login successful!', 'success');
                setTimeout(() => window.location.href = '/dashboard', 500);
            } else {
                this.showToast(resp.message || 'Login failed', 'error');
            }
        } catch (e) {
            this.showToast('Login failed. Please try again.', 'error');
        }
    },

    async register(data) {
        try {
            const resp = await this.apiCall('/auth/register', 'POST', data);
            if (resp.success) {
                this.token = resp.data.accessToken;
                this.showToast('Registration successful!', 'success');
                setTimeout(() => window.location.href = '/dashboard', 500);
            } else {
                this.showToast(resp.message || 'Registration failed', 'error');
            }
        } catch (e) {
            this.showToast('Registration failed.', 'error');
        }
    },

    async logout() {
        try {
            await this.apiCall('/auth/logout', 'POST');
        } catch (e) { /* ignore */ }
        this.token = null;
        localStorage.removeItem('wfh-refresh-token');
        window.location.href = '/login';
    },

    // ===== Toast Notifications =====
    showToast(message, type = 'info') {
        let container = document.getElementById('toastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toastContainer';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }
        const icons = { success: 'bi-check-circle-fill', error: 'bi-exclamation-circle-fill', warning: 'bi-exclamation-triangle-fill', info: 'bi-info-circle-fill' };
        const toast = document.createElement('div');
        toast.className = `toast-custom ${type}`;
        toast.innerHTML = `<i class="bi ${icons[type] || icons.info}"></i><span>${message}</span>
            <button onclick="this.parentElement.remove()" style="margin-left:auto;background:none;border:none;cursor:pointer;color:var(--text-muted)"><i class="bi bi-x"></i></button>`;
        container.appendChild(toast);
        setTimeout(() => toast.remove(), 5000);
    },

    // ===== Utility =====
    getCookie(name) {
        const v = document.cookie.match('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)');
        return v ? v.pop() : null;
    },

    formatDate(dateStr) {
        if (!dateStr) return '-';
        return new Date(dateStr).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
    },

    formatCurrency(amount) {
        if (!amount) return '-';
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
    },

    // ===== Employee Actions =====
    async deleteEmployee(id) {
        if (!confirm('Are you sure you want to delete this employee?')) return;
        const resp = await this.apiCall(`/employees/${id}`, 'DELETE');
        if (resp && resp.success) {
            this.showToast('Employee deleted', 'success');
            setTimeout(() => window.location.reload(), 500);
        } else {
            this.showToast('Failed to delete employee', 'error');
        }
    },

    // ===== Attendance =====
    async checkIn(employeeId) {
        const resp = await this.apiCall(`/attendance/check-in/${employeeId}`, 'POST');
        if (resp && resp.success) {
            this.showToast('Checked in successfully!', 'success');
            setTimeout(() => window.location.reload(), 500);
        } else {
            this.showToast(resp?.message || 'Check-in failed', 'error');
        }
    },

    async checkOut(employeeId) {
        const resp = await this.apiCall(`/attendance/check-out/${employeeId}`, 'POST');
        if (resp && resp.success) {
            this.showToast('Checked out successfully!', 'success');
            setTimeout(() => window.location.reload(), 500);
        } else {
            this.showToast(resp?.message || 'Check-out failed', 'error');
        }
    }
};

// Initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => WorkForceHub.init());
