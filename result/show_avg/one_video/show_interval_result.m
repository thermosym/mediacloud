clear all;
close all;

figure;
f1 = gca;
% run result_avg_lyap_t1.m;
% index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)','--r'); hold on;

% run result_avg_lyap_t2.m;
% index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)','--b'); hold on;
% 
run result_avg_lyap_t3.m;
index=1:1:size(lya_avg_delay(),1);
plot(index,lya_avg_delay(:,1)','-.k'); hold on;

run result_avg_lyap_t4.m;
index=1:1:size(lya_avg_delay(),1);
plot(f1,index,lya_avg_delay(:,1)','-*b'); hold on;

run result_avg_lyap_t5.m;
index=1:1:size(lya_avg_delay(),1);
plot(f1,index,lya_avg_delay(:,1)','-xr');hold on;


run result_avg_lyap_t6.m;
index=1:1:size(lya_avg_delay(),1);
plot(f1,index,lya_avg_delay(:,1)','-b');hold on;

run result_avg_lyap_t7.m;
index=1:1:size(lya_avg_delay(),1);
plot(f1,index,lya_avg_delay(:,1)',':r');hold on;

run result_avg_lyap_t10.m;
index=1:1:size(lya_avg_delay(),1);
plot(f1,index,lya_avg_delay(:,1)',':g');hold on;
% legend(f1,'3','4','4.5','5','5.5','6','7')
legend('3','4','5','6','7','8')
title(f1,'delay');


figure;
% run result_avg_lyap_t1.m;
% index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_quality(:,1)','--r'); hold on;

% run result_avg_lyap_t2.m;
% index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_quality(:,1)','--b'); hold on;
% 
run result_avg_lyap_t3.m;
index=1:1:size(lya_avg_delay(),1);
plot(index,lya_avg_quality(:,1)','-.k'); hold on;

run result_avg_lyap_t4.m;
index=1:1:size(lya_avg_delay(),1);
plot(index,lya_avg_quality(:,1)','-*b'); hold on;

run result_avg_lyap_t5.m;
index=1:1:size(lya_avg_delay(),1);
plot(index,lya_avg_quality(:,1)','-xr');hold on;

run result_avg_lyap_t6.m;
index=1:1:size(lya_avg_quality(),1);
plot(index,lya_avg_quality(:,1)','-b');hold on;

run result_avg_lyap_t7.m;
index=1:1:size(lya_avg_quality(),1);
plot(index,lya_avg_quality(:,1)',':r');hold on;

run result_avg_lyap_t10.m;
index=1:1:size(lya_avg_quality(),1);
plot(index,lya_avg_quality(:,1)',':g');hold on;

% run result_avg_lyap_t20.m;
% index=1:1:size(lya_avg_quality(),1);
% plot(index,lya_avg_quality(:,1)',':b');hold on;
% legend('3','4','4.5','5','5.5','6','7')
legend('3','4','5','6','7','8')
title('bitrate');


figure;
% run result_avg_lyap_t1.m;
% index=1:1:size(lya_avg_qlen(),1);
% plot(index,lya_avg_qlen(:,1)','--r'); hold on;

% run result_avg_lyap_t2.m;
% index=1:1:size(lya_avg_qlen(),1);
% plot(index,lya_avg_qlen(:,1)','--b'); hold on;

run result_avg_lyap_t3.m;
index=1:1:size(lya_avg_qlen(),1);
plot(index,lya_avg_qlen(:,1)','-.k'); hold on;

run result_avg_lyap_t4.m;
index=1:1:size(lya_avg_qlen(),1);
plot(index,lya_avg_qlen(:,1)','-*b'); hold on;

run result_avg_lyap_t5.m;
index=1:1:size(lya_avg_qlen(),1);
plot(index,lya_avg_qlen(:,1)','-xr');hold on;

run result_avg_lyap_t6.m;
index=1:1:size(lya_avg_qlen(),1);
plot(index,lya_avg_qlen(:,1)','-b');hold on;

run result_avg_lyap_t7.m;
index=1:1:size(lya_avg_qlen(),1);
plot(index,lya_avg_qlen(:,1)',':r');hold on;

run result_avg_lyap_t10.m;
index=1:1:size(lya_avg_qlen(),1);
plot(index,lya_avg_qlen(:,1)',':g');hold on;
% legend('3','4','4.5','5','5.5','6','7')
legend('3','4','5','6','7','8')
title('queueLen');

