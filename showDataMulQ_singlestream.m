clear all;close all;
run result_static;
%index = 1:1:length(task_delay);
%indexq = index + 10;
%plot(index_delay,task_delay,'.',index_qlen,task_qlen,'-',index_Sqlen,task_Sqlen,'--');
%legend('delay','Qlen','S-Qlen','Location','NorthWest');
Tslot = 0.1;
T = 5;
task_delay_0
slot_Qlen_0

t_Task_0 = 1:1:length(task_delay_0);

t_Slot = 1:1:length(slot_Qlen_0);

figure;
plot(t_Task_0*T,task_delay_0,'*-b');hold on;
legend('bbb','Location','NorthWest');
title('Task response delay');
xlabel('time of arrival(s)','FontSize',20,'FontWeight','bold');
ylabel('delay (s)','FontSize',20,'FontWeight','bold');

histIndex0 = 0:1:max(slot_Qlen_0);
histQlen0 = histc(slot_Qlen_0,histIndex0);
%histIndex = 0:1:max(max(slot_Qlen_0),max(slot_Qlen_1));
%histQlen = [histQlen0',histQlen1'];
figure;
maxQlen=max(histQlen0/sum(histQlen0));
bar(histIndex0,histQlen0/sum(histQlen0),'b');
title('Stream-A','FontSize',15,'FontWeight','bold');
ylim([0 maxQlen*1.1]);
xlabel('Queu length','FontSize',10,'FontWeight','bold');
ylabel('Probability','FontSize',10,'FontWeight','bold');

figure;
plot(t_Slot*Tslot,slot_Qlen_0,'*-b');
legend('bbb','ele','Location','NorthWest');
title('Queue length');
xlabel('time slot(s)','FontSize',20,'FontWeight','bold');
ylabel('Waiting job number','FontSize',20,'FontWeight','bold');

figure;
preset0 = task_preset_0;
index_preset = 1:1:length(task_preset_0);
plot(index_preset,preset0,'*--b');

figure;
plot(t_Slot*Tslot,slot_Zlen,'-b');