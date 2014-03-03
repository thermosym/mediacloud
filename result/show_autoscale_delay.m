%% bbb ele
close all;
clear all;
%%
figure;
run result_avg_lyap_b_e_N1.m;
index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)','-sb');hold on;
% plot(index,lya_avg_delay(:,2)','-^r');hold on;
plot(index,mean(lya_avg_delay,2),'-*k');hold on;

clear all;
run result_avg_lyap_b_e_N2.m;
index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)','-*b');hold on;
% plot(index,lya_avg_delay(:,2)','-xr');hold on;
plot(index,mean(lya_avg_delay,2),'-sk');hold on;

clear all;
run result_avg_lyap_b_e_N3.m;
index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)',':b');hold on;
% plot(index,lya_avg_delay(:,2)',':r');hold on;
plot(index,mean(lya_avg_delay,2),'-xk');hold on;

clear all;
run result_avg_lyap_b_e_N4.m;
index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)','--b');hold on;
% plot(index,lya_avg_delay(:,2)','--r');hold on;
plot(index,mean(lya_avg_delay,2),'--k');hold on;

clear all;
run result_avg_lyap_b_e_N5.m;
index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)','-b');hold on;
% plot(index,lya_avg_delay(:,2)','-r');hold on;
plot(index,mean(lya_avg_delay,2),'-k');hold on;

clear all;
run result_avg_lyap_b_e_N6.m;
index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)','-b');hold on;
% plot(index,lya_avg_delay(:,2)','-r');hold on;
plot(index,mean(lya_avg_delay,2),'.r');

legend('N=1','N=2','N=3','N=4','N=5','N=6','Location','NorthWest');
title('Delay performance for two video stream','FontSize',15,'FontWeight','bold');
xlabel('V','FontSize',15,'FontWeight','bold');
ylabel('Delay (s)','FontSize',15,'FontWeight','bold');

%% bbb
figure;
run result_avg_lyap_bbb_N1.m;
index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)','-sb');hold on;
% plot(index,lya_avg_delay(:,2)','-^r');hold on;
plot(index,mean(lya_avg_delay,2),'-*k');hold on;

clear all;
run result_avg_lyap_bbb_N2.m;
index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)','-*b');hold on;
% plot(index,lya_avg_delay(:,2)','-xr');hold on;
plot(index,mean(lya_avg_delay,2),'-sk');hold on;

clear all;
run result_avg_lyap_bbb_N3.m;
index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)',':b');hold on;
% plot(index,lya_avg_delay(:,2)',':r');hold on;
plot(index,mean(lya_avg_delay,2),'-k');hold on;

clear all;
run result_avg_lyap_bbb_N4.m;
index=1:1:size(lya_avg_delay(),1);
% plot(index,lya_avg_delay(:,1)','--b');hold on;
% plot(index,lya_avg_delay(:,2)','--r');hold on;
plot(index,mean(lya_avg_delay,2),'.r');hold on;

legend('N=1','N=2','N=3','N=4','Location','NorthWest');
title('Delay performance for one video stream','FontSize',15,'FontWeight','bold');
xlabel('V','FontSize',15,'FontWeight','bold');
ylabel('Delay (s)','FontSize',15,'FontWeight','bold');

% %% ele
% figure;
% run result_avg_lyap_ele_N1.m;
% index=1:1:size(lya_avg_delay(),1);
% % plot(index,lya_avg_delay(:,1)','-sb');hold on;
% % plot(index,lya_avg_delay(:,2)','-^r');hold on;
% plot(index,mean(lya_avg_delay,2),'-*k');hold on;
% 
% clear all;
% run result_avg_lyap_ele_N2.m;
% index=1:1:size(lya_avg_delay(),1);
% % plot(index,lya_avg_delay(:,1)','-*b');hold on;
% % plot(index,lya_avg_delay(:,2)','-xr');hold on;
% plot(index,mean(lya_avg_delay,2),'-sk');hold on;
% 
% clear all;
% run result_avg_lyap_ele_N3.m;
% index=1:1:size(lya_avg_delay(),1);
% % plot(index,lya_avg_delay(:,1)',':b');hold on;
% % plot(index,lya_avg_delay(:,2)',':r');hold on;
% plot(index,mean(lya_avg_delay,2),'-k');hold on;
% 
% clear all;
% run result_avg_lyap_ele_N4.m;
% index=1:1:size(lya_avg_delay(),1);
% % plot(index,lya_avg_delay(:,1)','--b');hold on;
% % plot(index,lya_avg_delay(:,2)','--r');hold on;
% plot(index,mean(lya_avg_delay,2),':r');hold on;
% 
% legend('N=1','N=2','N=3','N=4','Location','NorthWest');
% title('Delay performance for 1-4 number of Server','FontSize',15,'FontWeight','bold');
% xlabel('V','FontSize',15,'FontWeight','bold');
% ylabel('Delay (s)','FontSize',15,'FontWeight','bold');