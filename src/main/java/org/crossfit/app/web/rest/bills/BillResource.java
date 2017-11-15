package org.crossfit.app.web.rest.bills;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.BillLine;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.enumeration.BillStatus;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.service.BillService;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.dto.bills.BillGenerationParamDTO;
import org.crossfit.app.web.rest.dto.bills.BillPeriodDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Bill.
 */
@RestController
@RequestMapping("/api")
public class BillResource {


	private final Logger log = LoggerFactory.getLogger(BillResource.class);
	@Inject
	private BillService billService;

	@Inject
	private CrossFitBoxSerivce boxService;
	

	/**
	 * POST /bills -> Create a new bill.
	 */
//	@RequestMapping(value = "/bills", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Bill> create(@Valid @RequestBody Bill bill) throws URISyntaxException {
//		log.debug("REST request to save bill : {}", bill);
//		if (bill.getId() != null) {
//			return ResponseEntity.badRequest().header("Failure", "A new bill cannot already have an ID").body(null);
//		}
//		
//		CrossFitBox box = boxService.findCurrentCrossFitBox();
//		Bill result = billService.saveAndLockBill(box , bill.getMember(), bill.getStatus(), bill.getPaymentMethod(), bill.getEffectiveDate(), bill.getLines());
//		
//		return ResponseEntity.created(new URI("/api/bills/" + result.getId()))
//				.headers(HeaderUtil.createEntityCreationAlert("bill", result.getId().toString())).body(result);
//	}


	/**
	 * DELETE /bills/draft -> delete draft bills.
	 */
	@RequestMapping(value = "/bills/draft", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteDraft() throws URISyntaxException {
		log.debug("REST request to delete draft bills");
	
		billService.deleteDraftBills();
		
		return ResponseEntity.ok().build();
	}
    

	/**
	 * PUT /bills/generate -> Generate bill.
	 */
	@RequestMapping(value = "/bills/generate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> create(@Valid @RequestBody BillGenerationParamDTO param) throws URISyntaxException {
		log.debug("REST request to generate bill : {}", param);
	
		int totalBillGenerated = billService.generateBill(param.getSinceDate(), param.getUntilDate(), param.getAtDayOfMonth(), param.getStatus(), param.getPaymentMethod());
		
		return ResponseEntity.ok(totalBillGenerated);
	}
    
	/**
	 * GET /bills -> get all the bills.
	 */
	@RequestMapping(value = "/bills", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Bill>> getAll(
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "search", required = false) String search) throws URISyntaxException {
		
		Pageable generatePageRequest = PaginationUtil.generatePageRequest(offset, limit);
		
		Page<Bill> page = doFindAll(generatePageRequest, search);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bills", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}
	
	protected Page<Bill> doFindAll(Pageable generatePageRequest, String search) {
		search = search == null ? "" :search;
		String customSearch = "%" + search.replaceAll("\\*", "%").toLowerCase() + "%";
		return billService.findBills(customSearch, generatePageRequest);
	}

	/**
	 * GET /bills/periods -> get all the bills period.
	 */
	@RequestMapping(value = "/bills/periods", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillPeriodDTO>> getAllPeriods(){
		
		List<BillPeriodDTO> periods = billService.findBills("%", null).getContent().stream().map(BillPeriodDTO::new).sorted((p1,p2)->p1.getShortFormat().compareTo(p2.getShortFormat())).distinct().collect(Collectors.toList());
		
		return new ResponseEntity<>(periods, HttpStatus.OK);
	}

	/**
	 * GET /bills/paymentMethods -> get all the paymentMethods.
	 */
	@RequestMapping(value = "/bills/paymentMethods", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaymentMethod[]> getPaymentMethods(){
		return new ResponseEntity<>(PaymentMethod.values(), HttpStatus.OK);
	}
	/**
	 * GET /bills/status -> get all the bills status.
	 */
	@RequestMapping(value = "/bills/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BillStatus[]> getBillStatus(){
		return new ResponseEntity<>(BillStatus.values(), HttpStatus.OK);
	}



	/**
	 * GET /bills.csv -> get all the bills in CSV format.
	 */
	@RequestMapping(value = "/bills.csv", method = RequestMethod.GET, produces = "text/csv;charset=utf-8")
	public String getAllAsCSV(
			@RequestParam(value = "search", required = false) String search) throws URISyntaxException {
		
		Pageable generatePageRequest =  new PageRequest(0, Integer.MAX_VALUE);

		List<BillLine> billlines = doFindAll(generatePageRequest, search).getContent().stream().flatMap(b->b.getLines().stream()).collect(Collectors.toList());
		
		StringBuffer sb = new StringBuffer();

		sb.append("[Id];[FactNumber];[EffectiveDate];[CreatedDate];[MemberId];[Name];[Address];[Payment];[Status];[Quantity];[Label];[UnitPrice];[TotalPrice];[TotalFact]\n");		
		for (BillLine line : billlines) {
			append(sb, line.getBill().getId()).append(";");
			append(sb, line.getBill().getNumber()).append(";");
			append(sb, line.getBill().getEffectiveDate()).append(";");
			append(sb, line.getBill().getCreatedDate()).append(";");
			
			append(sb, line.getBill().getMember().getId()).append(";");
			append(sb, line.getBill().getDisplayName()).append(";");
			append(sb, line.getBill().getDisplayAddress()).append(";");
			
			append(sb, line.getBill().getPaymentMethod()).append(";");
			append(sb, line.getBill().getStatus()).append(";");
			append(sb, line.getQuantity()).append(";");
			append(sb, line.getLabel()).append(";");
			append(sb, line.getPriceTaxIncl()).append(";");
			append(sb, line.getTotalTaxIncl()).append(";");
			append(sb, line.getBill().getTotalTaxIncl()).append("\n");
		}
		return sb.toString();
	}
	

	private static final StringBuffer append(StringBuffer sb, Object value){
		sb.append("\"").append(value == null ? "" : value).append("\"");
		return sb;
	}
	
}
