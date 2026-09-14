document.addEventListener('DOMContentLoaded',()=>{
  const reveal=()=>document.querySelectorAll('.reveal').forEach((el,i)=>{const io=new IntersectionObserver(entries=>entries.forEach(e=>{if(e.isIntersecting){setTimeout(()=>e.target.classList.add('visible'),Math.min(i*45,260));io.unobserve(e.target)}}),{threshold:.08});io.observe(el)}); reveal();
  const toggle=document.querySelector('.nav-toggle'),nav=document.querySelector('.nav-links');if(toggle&&nav)toggle.addEventListener('click',()=>nav.classList.toggle('open'));
  document.querySelectorAll('[data-count]').forEach(el=>{const end=Number(el.dataset.count||0);let n=0;const step=Math.max(1,Math.ceil(end/24));const t=setInterval(()=>{n=Math.min(end,n+step);el.textContent=n;if(n>=end)clearInterval(t)},35)});
  document.querySelectorAll('input[name="cardNumber"]').forEach(input=>input.addEventListener('input',()=>{let v=input.value.replace(/\D/g,'').slice(0,16);input.value=v.replace(/(.{4})/g,'$1 ').trim()}));
});
