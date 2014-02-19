%% single video
clear all;
close all;
run result_single_static_faster.m;
slot_interval = 0.1;
task_interval = 5;

figure;
index_QlenAll = 1:1:size(slot_QlenAll,1);
QlenAll = max(slot_QlenAll,[],2);
QlenAll = cumsum(QlenAll')./index_QlenAll;
plot(index_QlenAll*slot_interval, QlenAll,'-b');
title('slot time average Qlen');

figure;
index_QBacklogAll = 1:1:size(slot_QBacklogAll,1);
QBacklogAll = max(slot_QBacklogAll,[],2);
QBacklogAll = cumsum(QBacklogAll')./index_QBacklogAll;
plot(index_QBacklogAll*slot_interval, QBacklogAll,'-b');
title('slot time average QBacklog');

figure;
index_task_0 = 1:1:size(task_delay_0,2);
avg_time_delay_0 = cumsum(task_delay_0)./index_task_0;
% plot(index_task_0,task_delay_0,'*b',index_task_0,avg_time_delay_0,'-b');
plot(index_task_0*task_interval,avg_time_delay_0,'--b');
title('task delay');

figure;
Mbps = 1000;
index_task_0 = 1:1:size(task_quality_0,2);
avg_quality_0 = cumsum(task_quality_0)./index_task_0;
%plot(index_task_0,task_quality_0,'*b',index_task_0,avg_quality_0,'-b',...
%     index_task_1,task_quality_1,'sr',index_task_1,avg_quality_1,'-r');
plot(index_task_0*task_interval,avg_quality_0/Mbps,'--b');
title('task quality');


%% two videos

clear all;
close all;
run result_single_static_faster.m;
slot_interval = 0.1;
task_interval = 5;

figure;
index_QlenAll = 1:1:size(slot_QlenAll,1);
QlenAll = max(slot_QlenAll,[],2);
QlenAll = cumsum(QlenAll')./index_QlenAll;
plot(index_QlenAll*slot_interval, QlenAll,'-b');
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
