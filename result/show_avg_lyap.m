
%% only one video
clear all;
close all;
run result_avg_lyap_b_e_N1.m;

index=1:1:size(lya_avg_delay(),1);

figure;
plot(index,lya_avg_delay(:,1)','-*');
title('delay');
figure;
plot(index,lya_avg_quality(:,1)','-*');
title('bitrate');
figure;
plot(index,lya_avg_qlen(:,1)','-*');
title('qlen');
figure;
plot(index,lya_avg_qbacklog(:,1)','-*')
title('backlog')

figure
[AX,H1,H2] = plotyy(index,lya_avg_quality,index,lya_avg_delay,'plot')
set(get(AX(1),'Ylabel'),'String','Average Bitrate (Kb/s)','FontSize',15,'FontWeight','bold') 
set(get(AX(2),'Ylabel'),'String','Average Delay (s)','FontSize',15,'FontWeight','bold') 
xlabel('V','FontSize',12,'FontWeight','bold')
title('Compression Performance and Delay vs. V','FontSize',15,'FontWeight','bold') 
set(H1,'LineStyle','-','Marker','*')
set(H2,'LineStyle',':','Marker','.')
legend('Bitrate-bbb','Bitrate-ele','Delay-bbb','Delay-ele','Location','NorthWest');
%% two video
clear all;
close all;
run result_avg_lyap.m;

index=1:1:size(lya_avg_delay(),1);

figure;
plot(index,lya_avg_delay(:,1)','-*b');hold on;
plot(index,lya_avg_delay(:,2)','-sr');
title('delay');
figure;
plot(index,lya_avg_quality(:,1)','-*b');hold on;
plot(index,lya_avg_quality(:,2)','-sr');
title('bitrate');
figure;
plot(index,lya_avg_qlen(:,1)','-*b');
title('qlen');
figure;
plot(index,lya_avg_qbacklog(:,1)','-*b');
title('backlog');

