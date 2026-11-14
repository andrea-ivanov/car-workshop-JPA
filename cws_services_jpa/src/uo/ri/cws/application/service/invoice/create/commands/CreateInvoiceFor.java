package uo.ri.cws.application.service.invoice.create.commands;

import java.util.List;

import uo.ri.conf.Factories;
import uo.ri.cws.application.query.InvoiceQueries;
import uo.ri.cws.application.repository.InvoiceRepository;
import uo.ri.cws.application.repository.WorkOrderRepository;
import uo.ri.cws.application.service.invoice.InvoicingService.InvoiceDto;
import uo.ri.cws.application.service.invoice.create.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Invoice;
import uo.ri.cws.domain.WorkOrder;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

public class CreateInvoiceFor implements Command<InvoiceDto> {

	private List<String> workOrderIds;
	private WorkOrderRepository wRepo = Factories.repository.forWorkOrder();
	private InvoiceQueries iQueries = Factories.query.forInvoice();
	private InvoiceRepository iRepo = Factories.repository.forInvoice();

	public CreateInvoiceFor(List<String> workOrderIds) {
		ArgumentChecks.isNotNull(workOrderIds);
		ArgumentChecks.isFalse(workOrderIds.isEmpty());
		ArgumentChecks.isFalse(workOrderIds.stream().anyMatch(i -> i == null));

		this.workOrderIds = workOrderIds;
	}

	@Override
	public InvoiceDto execute() throws BusinessException {

		List<WorkOrder> workOrders = wRepo.findByIds(workOrderIds);
		BusinessChecks.isTrue(workOrderIds.size() == workOrders.size(), "Las ordenes de trabajo no coinciden");

		for (WorkOrder w : workOrders)
			BusinessChecks.isTrue(w.isApproved(), "No todas las ordenes de trabajo están terminadas");

		// sigiente número de factura del dominio
		long number = iQueries.getNextInvoiceNumber();

		Invoice invoice = new Invoice(number, workOrders);
		iRepo.add(invoice);

		return DtoAssembler.toDto(invoice);
	}

}
