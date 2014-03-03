clear all;
close all;
run result_avg_static_bbb_N1.m;

index=1:1:size(sta_avg_delay(),1);

%% only one video
figure;
plot(index,sta_avg_delay(:,1)','-*');
title('delay');
figure;
plot(index,sta_avg_quality(:,1)','-*');
title('bitrate');
figure;
plot(index,sta_avg_qlen(:,1)','-*');
title('qlen');
figure;
plot(index,sta_avg_qbacklog(:,1)','-*');
title('backlog');

%% two video
close all;
figure;
plot(index,sta_avg_delay(:,1)','-*b');hold on;
plot(index,sta_avg_delay(:,2)','-sr');
title('delay');
figure;
plot(index,sta_avg_quality(:,1)','-*b');hold on;
plot(index,sta_avg_quality(:,2)','-sr');
title('bitrate');
figure;
plot(index,sta_avg_qlen(:,1)','-*b');
title('qlen');
figure;
plot(index,sta_avg_qbacklog(:,1)','-*b');
title('backlog');

