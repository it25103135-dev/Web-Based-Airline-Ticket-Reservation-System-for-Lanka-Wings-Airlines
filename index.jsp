<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html><html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Lanka Wings Airlines</title><link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css"></head>
<body class="home-page">
<header class="home-nav"><a class="brand light" href="<%=request.getContextPath()%>/"><span class="brand-mark">LW</span><span>Lanka <b>Wings</b></span></a><nav><a href="<%=request.getContextPath()%>/login">Login</a><a class="btn btn-orange small" href="<%=request.getContextPath()%>/register">Create Account</a></nav></header>
<section class="hero">
  <div class="hero-overlay"></div><div class="hero-cloud c1"></div><div class="hero-cloud c2"></div><div class="flying-plane">✈</div>
  <div class="hero-content reveal"><span class="eyebrow">WELCOME ABOARD</span><h1>Your Lanka Wings<br><em>account portal.</em></h1><div class="hero-actions"><a class="btn btn-orange" href="<%=request.getContextPath()%>/login">Sign In</a><a class="btn btn-glass" href="<%=request.getContextPath()%>/register">Create Account</a></div><div class="trust-row"><span>✓ Secure Login</span><span>✓ Profile Management</span><span>✓ Notifications</span></div></div>
</section>
<section class="section"><div class="section-heading reveal"><span class="eyebrow navy">ACCOUNT ACCESS</span><h2>Manage your Lanka Wings account</h2><p>Create an account, sign in securely, update your profile and view important notifications.</p></div><div class="feature-grid">
<div class="feature-card reveal"><div class="icon">👤</div><h3>User Registration</h3><p>Create a passenger account using your personal and login details.</p></div>
<div class="feature-card reveal"><div class="icon">🔐</div><h3>Secure Login</h3><p>Sign in using your credentials and access protected account pages.</p></div>
<div class="feature-card reveal"><div class="icon">🔔</div><h3>Notifications</h3><p>View account and travel notifications and mark alerts as read.</p></div>
</div></section>
<%@ include file="WEB-INF/jspf/footer.jspf" %></body></html>
