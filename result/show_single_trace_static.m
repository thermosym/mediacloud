%% single video
clear all;
close all;
% run bbb_trans_trace_result_single_static_faster.m;
% run bbb_trans_trace_result_single_lyap_faster.m;
run bbb_trans_trace_result_single_static_slow_n1.m;
run bbb_trans_trace_result_single_lyap_slow_n1.m;

slot_interval = 0.1;
task_interval = 5;

figure;
sta_index_QlenAll = 1:1:size(sta_slot_QlenAll,1);
lya_index_QlenAll = 1:1:size(lya_slot_QlenAll,1);
sta_QlenAll = max(sta_slot_QlenAll,[],2);
lya_QlenAll = max(lya_slot_QlenAll,[],2);
sta_QlenAll = cumsum(sta_QlenAll')./sta_index_QlenAll;
lya_QlenAll = cumsum(lya_QlenAll')./lya_index_QlenAll;
plot(sta_index_QlenAll*slot_interval, sta_QlenAll,'-b',lya_index_QlenAll*slot_interval, lya_QlenAll,'-r');
title('slot time average Qlen');

figure;
sta_index_QBacklogAll = 1:1:size(sta_slot_QBacklogAll,1);
lya_index_QBacklogAll = 1:1:size(lya_slot_QBacklogAll,1);
sta_QBacklogAll = max(sta_slot_QBacklogAll,[],2);
lya_QBacklogAll = max(lya_slot_QBacklogAll,[],2);
sta_QBacklogAll = cumsum(sta_QBacklogAll')./sta_index_QBacklogAll;
lya_QBacklogAll = cumsum(lya_QBacklogAll')./lya_index_QBacklogAll;
plot(sta_index_QBacklogAll*slot_interval, sta_QBacklogAll,'-b',lya_index_QBacklogAll*slot_interval, lya_QBacklogAll,'-r');
title('slot time average QBacklog');

figure;
sta_index_task_0 = 1:1:size(sta_task_delay_0,2);
lya_index_task_0 = 1:1:size(lya_task_delay_0,2);
sta_avg_time_delay_0 = cumsum(sta_task_delay_0)./sta_index_task_0;
lya_avg_time_delay_0 = cumsum(lya_task_delay_0)./lya_index_task_0;
plot(sta_index_task_0*task_interval,sta_task_delay_0,'*b');hold on;
plot(sta_index_task_0*task_interval,sta_avg_time_delay_0,'--b','LineWidth',2); hold on;
plot(lya_index_task_0*task_interval,lya_task_delay_0,'xr'); hold on;
plot(lya_index_task_0*task_interval,lya_avg_time_delay_0,'-r','LineWidth',2);
%plot(sta_index_task_0*task_interval,sta_avg_time_delay_0,'--b',lya_index_task_0*task_interval,lya_avg_time_delay_0,'--r');
title('Job delay performance of BBB video','FontSize',15,'FontWeight','bold');
legend('static: delay trace','static: average delay','our: delay trace','our: average delay','location','NorthWest');
xlabel('Time sequence(s)','FontSize',15,'FontWeight','bold');
ylabel('Job completion delay time(s)','FontSize',15,'FontWeight','bold');

figure;
Mbps = 1000;
sta_index_task_0 = 1:1:size(sta_task_quality_0,2);
lya_index_task_0 = 1:1:size(lya_task_quality_0,2);
sta_avg_quality_0 = cumsum(sta_task_quality_0)./sta_index_task_0;
lya_avg_quality_0 = cumsum(lya_task_quality_0)./lya_index_task_0;
%plot(index_task_0,task_quality_0,'*b',index_task_0,avg_quality_0,'-b',...
%     index_task_1,task_quality_1,'sr',index_task_1,avg_quality_1,'-r');
plot(sta_index_task_0*task_interval,sta_avg_quality_0/Mbps,'--b','LineWidth',2); hold on;
plot(lya_index_task_0*task_interval,lya_avg_quality_0/Mbps,'-r','LineWidth',2);
title('Job bitrate performance of BBB video','FontSize',15,'FontWeight','bold');
legend('static','our','location','NorthEast');
xlabel('Time sequence(s)','FontSize',15,'FontWeight','bold');
ylabel('Average bitrate (Mbps)','FontSize',15,'FontWeight','bold');
xlim([100 600])

%% two videos

clear all;
close all;
% run bbb_trans_trace_result_single_static_faster.m;
% run bbb_trans_trace_result_single_lyap_faster.m;
run bbb_ele_trace_result_single_static_faster_n2.m
%run bbb_ele_trace_result_single_static_faster_n5_v100.m
inx_len_faster = min(size(sta_task_delay_0,2),size(sta_task_delay_1,2));
index_task_faster = 1:1:inx_len_faster;
sta_task_delay_faster = (sta_task_delay_0(1:inx_len_faster) + sta_task_delay_1(1:inx_len_faster))/2;
acc_sta_task_delay_faster = cumsum(sta_task_delay_faster)./index_task_faster;
sta_task_quality_faster = (sta_task_quality_0(1:inx_len_faster) + sta_task_quality_1(1:inx_len_faster))/2;
acc_sta_task_quality_faster = cumsum(sta_task_quality_faster)./index_task_faster;

run bbb_ele_trace_result_single_static_slow_n2.m;
run bbb_ele_trace_result_single_lyap_slow_n2.m;
%run bbb_ele_trace_result_single_static_slow_n5_v100.m;
%run bbb_ele_trace_result_single_lyap_slow_n5_v100;

slot_interval = 0.1;
task_interval = 5;

figure;
inx_len = min(size(sta_task_delay_0,2),size(sta_task_delay_1,2));
index_task = 1:1:inx_len;
sta_task_delay = (sta_task_delay_0(1:inx_len) + sta_task_delay_1(1:inx_len))/2;
lya_task_delay = (lya_task_delay_0(1:inx_len) + lya_task_delay_1(1:inx_len))/2;
acc_sta_task_delay = cumsum(sta_task_delay)./index_task;
acc_lya_task_delay = cumsum(lya_task_delay)./index_task;

plot(index_task*task_interval,sta_task_delay,'*b');hold on;
plot(index_task*task_interval,acc_sta_task_delay,'--b','LineWidth',2); hold on;
plot(index_task_faster*task_interval,sta_task_delay_faster,'+g');hold on;
plot(index_task_faster*task_interval,acc_sta_task_delay_faster,'--g','LineWidth',2); hold on;

plot(index_task*task_interval,lya_task_delay,'xr'); hold on;
plot(index_task*task_interval,acc_lya_task_delay,'-r','LineWidth',2);
%plot(sta_index_task_0*task_interval,sta_avg_time_delay_0,'--b',lya_index_task_0*task_interval,lya_avg_time_delay_0,'--r');
title('Job delay performance of two video','FontSize',15,'FontWeight','bold');
legend('static delay trace: slow','static average delay: slow',...
    'static delay trace: faster','static average delay: faster',...
    'our delay trace','our average delay','location','NorthWest');
xlabel('Time sequence(s)','FontSize',15,'FontWeight','bold');
ylabel('Job completion delay time(s)','FontSize',15,'FontWeight','bold');

figure;
Mbps = 1000;
sta_task_quality = (sta_task_quality_0(1:inx_len) + sta_task_quality_1(1:inx_len))/2;
lya_task_quality = (lya_task_quality_0(1:inx_len) + lya_task_quality_1(1:inx_len))/2;
acc_sta_task_quality = cumsum(sta_task_quality)./index_task;
acc_lya_task_quality = cumsum(lya_task_quality)./index_task;
%plot(index_task_0,task_quality_0,'*b',index_task_0,avg_quality_0,'-b',...
%     index_task_1,task_quality_1,'sr',index_task_1,avg_quality_1,'-r');
plot(index_task*task_interval,acc_sta_task_quality/Mbps,'--b','LineWidth',2); hold on;
plot(index_task_faster*task_interval,acc_sta_task_quality_faster/Mbps,'-.g','LineWidth',2); hold on;
plot(index_task*task_interval,acc_lya_task_quality/Mbps,'-r','LineWidth',2);
title('Job bitrate performance of two video','FontSize',15,'FontWeight','bold');
legend('static: slow','static: faster','our','location','NorthEast');
xlabel('Time sequence(s)','FontSize',15,'FontWeight','bold');
ylabel('Average bitrate (Mbps)','FontSize',15,'FontWeight','bold');



%%
figure;
sta_index_QlenAll = 1:1:size(sta_slot_QlenAll,1);
QlenAll = max(sta_slot_QlenAll,[],2);
QlenAll = cumsum(QlenAll')./sta_index_QlenAll;
plot(sta_index_QlenAll*slot_interval, QlenAll,'-b');
title('slot time average Qlen');

figure;
index_QBacklogAll = 1:1:size(slot_QBacklogAll,1);
QBacklogAll = max(slot_QBacklogAll,[],2);
QBacklogAll = cumsum(QBacklogAll')./index_QBacklogAll;
plot(index_QBacklogAll*slot_interval, QBacklogAll,'-b');
title('slot time average QBacklog');

figure;
index_task_0 = 1:1:size(task_delay_0,2);
index_task_1 = 1:1:size(task_delay_1,2);
avg_time_delay_0 = cumsum(task_delay_0)./index_task_0;
avg_time_delay_1 = cumsum(task_delay_1)./index_task_1;
% plot(index_task_0,task_delay_0,'*b',index_task_0,avg_time_delay_0,'-b',...
%     index_task_1,task_delay_1,'sr',index_task_1,avg_time_delay_1,'-r');
plot(index_task_0*task_interval,avg_time_delay_0,'--b',index_task_1*task_interval,avg_time_delay_1,'--r');
title('task delay');

figure;
Mbps = 1000;
index_task_0 = 1:1:size(task_quality_0,2);
index_task_1 = 1:1:size(task_quality_1,2);
avg_quality_0 = cumsum(task_quality_0)./index_task_0;
avg_quality_1 = cumsum(task_quality_1)./index_task_1;
%plot(index_task_0,task_quality_0,'*b',index_task_0,avg_quality_0,'-b',...
%     index_task_1,task_quality_1,'sr',index_task_1,avg_quality_1,'-r');
plot(index_task_0*task_interval,avg_quality_0/Mbps,'--b',index_task_1*task_interval,avg_quality_1/Mbps,'--r');
title('task quality');
