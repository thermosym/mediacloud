%% single video
clear all;
close all;
v = 1000;
slot_interval = 0.1;
task_interval = 5;

figure;
%plot(sta_index_task_0*task_interval,sta_task_delay_0,'*b');hold on;
%plot(lya_index_task_0*task_interval,lya_task_delay_0,'xr'); hold on;
%legend('static: delay trace','static: average delay','our: delay trace','our: average delay','location','NorthWest');

run bbb_trans_trace_result_single_lyap_medium.m;
lya_index_task_0 = 1:1:size(lya_task_delay_0,2);
lya_task_delay_0 = mean([lya_task_delay_0;lya_task_delay_1],1);
lya_avg_time_delay_0 = cumsum(lya_task_delay_0)./lya_index_task_0;
% lya_avg_time_delay_1 = cumsum(lya_task_delay_1)./lya_index_task_0;
% lya_avg_time_delay = mean([lya_avg_time_delay_0; lya_avg_time_delay_1],1);
plot(lya_index_task_0*task_interval,lya_avg_time_delay_0,'-k','LineWidth',2); hold on;

run bbb_trans_trace_result_single_static_superfast.m;
sta_index_task_0 = 1:1:size(sta_task_delay_0,2);
sta_task_delay_0 = mean([sta_task_delay_0;sta_task_delay_1],1);
sta_avg_time_delay_0 = cumsum(sta_task_delay_0)./sta_index_task_0;
plot(sta_index_task_0*task_interval,sta_avg_time_delay_0,'--b','LineWidth',2); hold on;

run bbb_trans_trace_result_single_static_faster.m;
sta_index_task_0 = 1:1:size(sta_task_delay_0,2);
sta_task_delay_0 = mean([sta_task_delay_0;sta_task_delay_1],1);
sta_avg_time_delay_0 = cumsum(sta_task_delay_0)./sta_index_task_0;
plot(sta_index_task_0*task_interval,sta_avg_time_delay_0,'--g','LineWidth',2); hold on;

run bbb_trans_trace_result_single_static_medium.m;
sta_index_task_0 = 1:1:size(sta_task_delay_0,2);
sta_task_delay_0 = mean([sta_task_delay_0;sta_task_delay_1],1);
sta_avg_time_delay_0 = cumsum(sta_task_delay_0)./sta_index_task_0;
plot(sta_index_task_0*task_interval,sta_avg_time_delay_0,'--r','LineWidth',2); hold on;

title('Job delay performance of BBB video','FontSize',15,'FontWeight','bold');
legend('our','static-superfast','static-faster','static-medium','location','NorthWest');
xlabel('Time sequence(s)','FontSize',15,'FontWeight','bold');
ylabel('Job completion delay time(s)','FontSize',15,'FontWeight','bold');
xlim([1000*task_interval max(lya_index_task_0)*task_interval])

figure;
Mbps = 1000;
run bbb_trans_trace_result_single_lyap_medium.m;
lya_index_task_0 = 1:1:size(lya_task_quality_0,2);
lya_task_quality_0 = mean([lya_task_quality_0;lya_task_quality_1],1);
lya_avg_quality_0 = cumsum(lya_task_quality_0)./lya_index_task_0;
plot(lya_index_task_0*task_interval,lya_avg_quality_0/Mbps,'-k','LineWidth',2); hold on;


run bbb_trans_trace_result_single_static_superfast.m;
sta_index_task_0 = 1:1:size(sta_task_quality_0,2);
sta_task_quality_0 = mean([sta_task_quality_0;sta_task_quality_1],1);
sta_avg_quality_0 = cumsum(sta_task_quality_0)./sta_index_task_0;
plot(sta_index_task_0*task_interval,sta_avg_quality_0/Mbps,'--b','LineWidth',2); hold on;


run bbb_trans_trace_result_single_static_faster.m;
sta_index_task_0 = 1:1:size(sta_task_quality_0,2);
sta_task_quality_0 = mean([sta_task_quality_0;sta_task_quality_1],1);
sta_avg_quality_0 = cumsum(sta_task_quality_0)./sta_index_task_0;
plot(sta_index_task_0*task_interval,sta_avg_quality_0/Mbps,'--g','LineWidth',2); hold on;

run bbb_trans_trace_result_single_static_medium.m;
sta_index_task_0 = 1:1:size(sta_task_quality_0,2);
sta_task_quality_0 = mean([sta_task_quality_0;sta_task_quality_1],1);
sta_avg_quality_0 = cumsum(sta_task_quality_0)./sta_index_task_0;
plot(sta_index_task_0*task_interval,sta_avg_quality_0/Mbps,'--r','LineWidth',2); hold on;

title('Job bitrate performance of BBB video','FontSize',15,'FontWeight','bold');
legend('our','static-superfast','static-faster','static-medium','location','NorthWest');
xlabel('Time sequence(s)','FontSize',15,'FontWeight','bold');
ylabel('Average bitrate (Mbps)','FontSize',15,'FontWeight','bold');
xlim([1000*task_interval max(lya_index_task_0)*task_interval])

%%
% figure;
% sta_index_QlenAll = 1:1:size(sta_slot_QlenAll,1);
% sta_QlenAll = max(sta_slot_QlenAll,[],2);
% sta_QlenAll = cumsum(sta_QlenAll')./sta_index_QlenAll;
% 
% lya_index_QlenAll = 1:1:size(lya_slot_QlenAll,1);
% lya_QlenAll = max(lya_slot_QlenAll,[],2);
% lya_QlenAll = cumsum(lya_QlenAll')./lya_index_QlenAll;
% 
% plot(sta_index_QlenAll*slot_interval, sta_QlenAll,'-b',lya_index_QlenAll*slot_interval, lya_QlenAll,'-r');
% title('slot time average Qlen');
% 
% figure;
% sta_index_QBacklogAll = 1:1:size(sta_slot_QBacklogAll,1);
% sta_QBacklogAll = max(sta_slot_QBacklogAll,[],2);
% sta_QBacklogAll = cumsum(sta_QBacklogAll')./sta_index_QBacklogAll;
% 
% lya_index_QBacklogAll = 1:1:size(lya_slot_QBacklogAll,1);
% lya_QBacklogAll = max(lya_slot_QBacklogAll,[],2);
% lya_QBacklogAll = cumsum(lya_QBacklogAll')./lya_index_QBacklogAll;
% 
% plot(sta_index_QBacklogAll*slot_interval, sta_QBacklogAll,'-b',lya_index_QBacklogAll*slot_interval, lya_QBacklogAll,'-r');
% title('slot time average QBacklog');
