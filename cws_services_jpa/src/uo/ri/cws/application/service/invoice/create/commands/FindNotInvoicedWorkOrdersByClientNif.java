package uo.ri.cws.application.service.invoice.create.commands;

import java.util.List;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.WorkOrderRepository;
import uo.ri.cws.application.service.invoice.InvoicingService.InvoicingWorkOrderDto;
import uo.ri.cws.application.service.invoice.create.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.WorkOrder;
import uo.ri.cws.domain.types.Nif;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessException;

public class FindNotInvoicedWorkOrdersByClientNif implements Command<List<InvoicingWorkOrderDto>> {

    private String nif;
    private WorkOrderRepository wRepo = Factories.repository.forWorkOrder();

    public FindNotInvoicedWorkOrdersByClientNif(String nif) {
        ArgumentChecks.isNotBlank(nif, "El nif del cliente no puede estar en blanco");
        this.nif = nif;
    }

    @Override
    public List<InvoicingWorkOrderDto> execute() throws BusinessException {
        List<WorkOrder> workOrders = wRepo.findNotInvoicedByClientNif(new Nif(nif));
        return DtoAssembler.toInvoicingWorkOrderDtoList(workOrders);
    }

}
