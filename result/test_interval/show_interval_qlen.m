clear all;
figure;
f3=gca;

run result_avg_lyap_t2.m;
index=1:1:size(lya_avg_delay(),1);
plot(f3,index,lya_avg_qlen(:,1)','--b'); hold on;

run result_avg_lyap_t3.m;
index=1:1:size(lya_avg_delay(),1);
plot(f3,index,lya_avg_qlen(:,1)','-.k'); hold on;

run result_avg_lyap_t4.m;
index=1:1:size(lya_avg_delay(),1);
plot(f3,index,lya_avg_qlen(:,1)','-*b'); hold on;


run result_avg_lyap_t5.m;
index=1:1:size(lya_avg_delay(),1);
plot(f3,index,lya_avg_qlen(:,1)','-xr'); hold on;


run result_avg_lyap_t6.m;
index=1:1:size(lya_avg_delay(),1);
plot(f3,index,lya_avg_qlen(:,1)','-b'); hold on;

run result_avg_lyap_t7.m;
index=1:1:size(lya_avg_delay(),1);
plot(f3,index,lya_avg_qlen(:,1)',':r'); hold on;

run result_avg_lyap_t8.m;
index=1:1:size(lya_avg_delay(),1);
plot(f3,index,lya_avg_qlen(:,1)',':g'); hold on;

run result_avg_lyap_t15.m;
index=1:1:size(lya_avg_delay(),1);
plot(f3,index,lya_avg_qlen(:,1)',':y'); hold on;

run result_avg_lyap_t20.m;
index=1:1:size(lya_avg_delay(),1);
plot(f3,index,lya_avg_qlen(:,1)',':b'); hold on;

title(f3,'queueLen');

legend(f3,'2','3','4','5','6','7','8','15','20')