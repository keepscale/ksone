package org.crossfit.app.web.rest.bills;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.BillLine;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.enumeration.BillStatus;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.repository.BillsBucket;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.service.BillService;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.PdfBill;
import org.crossfit.app.web.rest.dto.bills.BillPeriodDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.xmp.XMPException;
import com.opencsv.CSVWriter;

/**
 * REST controller for managing Bill.
 */
@RestController
@RequestMapping("/api")
public class BillResource {
	public enum DetinationGeneration {
    	CSV, DATABASE
	}

	public class InMemoryBillRepository implements BillsBucket{

		private List<Bill> bills = new ArrayList<>();
		
		@Override
		public Page<Bill> findAllBillNumberLikeForBoxOrderByNumberDesc(String like, CrossFitBox box,
				Pageable pageable) {
			
			return new PageImpl<>(bills.stream()
					.filter(b->{
						return b.getBox().equals(box) && b.getNumber().startsWith(like.replace("%", ""));
					})
					.sorted(Comparator.comparing(Bill::getNumber))
					.collect(Collectors.toList()));
			
		}

		@Override
		public Bill save(Bill bill) {
			bills.add(bill);
			return bill;
		}

	}


	private final Logger log = LoggerFactory.getLogger(BillResource.class);
	@Inject
	private BillService billService;

	@Inject
	private CrossFitBoxSerivce boxService;
	@Inject
	private MemberRepository memberRepository;
	

	/**
	 * POST /bills -> Create a new bill.
	 */
	@RequestMapping(value = "/bills", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Bill> create(@Valid @RequestBody Bill bill) throws URISyntaxException {
		log.debug("REST request to save bill : {}", bill);
		if (bill.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new bill cannot already have an ID").body(null);
		}
		
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		Member member = memberRepository.findOne(bill.getMember().getId());
		Bill result = billService.saveAndLockBill(box , member, bill.getStatus(), bill.getPaymentMethod(), bill.getEffectiveDate(), bill.getPayAtDate(), bill.getLines());
		
		return ResponseEntity.created(new URI("/api/bills/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("bill", result.getId().toString())).body(result);
	}


	/**
	 * DELETE /bills/draft -> delete draft bills.
	 */
	@RequestMapping(value = "/bills/draft", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteDraft() throws URISyntaxException {
		log.debug("REST request to delete draft bills");
	
		billService.deleteDraftBills(boxService.findCurrentCrossFitBox());
		
		return ResponseEntity.ok().build();
	}
    

	/**
	 * PUT /bills/generate -> Generate bill.
	 * @param since 
	 * @param until 
	 * @param atDayOfMonth 
	 * @param status 
	 * @param dest 
	 * @throws Exception 
	 */
	@RequestMapping(value = "/bills/generate", method = RequestMethod.GET)
	public void generate(HttpServletResponse response, 
			@RequestParam(value = "sinceDate", required = true) String sinceStr, 
			@RequestParam(value = "untilDate", required = true) String untilStr, 
			@RequestParam(value = "atDayOfMonth", required = true) int atDayOfMonth, 
			@RequestParam(value = "status", required = true) BillStatus status, 
			@RequestParam(value = "dest", required = true) DetinationGeneration dest) throws Exception {
		log.debug("REST request to generate bill : {}", dest);
	
				
		LocalDate since = ISODateTimeFormat.dateTimeParser().parseDateTime(sinceStr).toLocalDate();
		LocalDate until = ISODateTimeFormat.dateTimeParser().parseDateTime(untilStr).toLocalDate();
		
		if (dest == DetinationGeneration.CSV) {
			InMemoryBillRepository billsBucket = new InMemoryBillRepository();
			billService.generateBill(since, until, atDayOfMonth, status, billsBucket);
			
			StringWriter sw = new StringWriter();
			writeToCSV(billsBucket.bills, sw);
			
			response.setContentType("text/csv;charset=ISO-8859-1");
			response.getOutputStream().write(sw.toString().getBytes("ISO-8859-1"));
			response.setHeader("Content-Disposition", "attachment; filename=\"bills-" + since + "-" + until + ".csv\"");
			response.flushBuffer();
			
		}
		else {
			billService.generateBill(since, until, atDayOfMonth, status);
		}
		
	}

	/**
	 * GET /members/{id} -> get a bill.
	 */
	@RequestMapping(value = "/bills/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Bill> get(@PathVariable Long id) {
		log.debug("REST request to get Bill : {}", id);
		return Optional.ofNullable(doGet(id))
				.map(bill -> new ResponseEntity<>(bill, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	

	/**
	 * GET /members/{id}.pdf -> get a bill in pdf format.
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws XMPException 
	 * @throws DocumentException 
	 * @throws TransformerException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@RequestMapping(value = "/bills/{id}.pdf", method = RequestMethod.GET, produces = "application/pdf")
	public void getToPdf(@PathVariable Long id, HttpServletResponse response) throws IOException, ParserConfigurationException, SAXException, TransformerException, DocumentException, XMPException, ParseException{
		log.debug("REST request to get PdfBill : {}", id);

		PdfBill.getBuilder().createPdf(doGet(id), response.getOutputStream());

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".pdf\"");
		response.flushBuffer();

	}

	protected Bill doGet(Long id) {
		Bill bill = 
				this.cleanMembershipRules().convert(billService.findById(id, boxService.findCurrentCrossFitBox()));
		return bill;
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
		return billService.findBills(customSearch, generatePageRequest).map(cleanMembershipRules());
	}


	private Converter<? super Bill, ? extends Bill> cleanMembershipRules() {
		return bill->{
			if (bill !=null)
				bill.getLines().stream().forEach(line->{
					if (line.getSubscription() != null)
						line.getSubscription().getMembership().setMembershipRules(null);
				});
			return bill;
		};
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
	 * @throws Exception 
	 */
	@RequestMapping(value = "/bills.csv", method = RequestMethod.GET, produces = "text/csv;charset=ISO-8859-1")
	public String getAllAsCSV(
			@RequestParam(value = "search", required = false) String search) throws Exception {
		
		
		Pageable generatePageRequest =  new PageRequest(0, Integer.MAX_VALUE);

		List<Bill> bills = doFindAll(generatePageRequest, search).getContent();
		
		StringWriter sw = new StringWriter();
		
		
		writeToCSV(bills, sw);
		
		
		return sw.toString();
	}


	private void writeToCSV(List<Bill> bills, Writer sw) throws Exception {
		List<BillLine> billlines = bills.stream().flatMap(b->b.getLines().stream()).collect(Collectors.toList());
		try (CSVWriter writer = new CSVWriter(sw, ';')){
			String[] header = new StringBuffer("[FactId];[FactNumber];[EffectiveDate];[CreatedDate];[MemberId];[MemberName];[MemberAddress];[Payment];[Status];[Quantity];[Label];[UnitPrice];[TotalPrice];[SubscriptionId];[SubscriptionStart];[SubscriptionEnd];[PeriodStart];[PeriodEnd];[totalBookingOnPeriod];[totalBooking];[TotalFact]").toString().split(";");		
			writer.writeNext(header, false);
			for (BillLine line : billlines) {
				String[] columns = new String[header.length];
				
				columns[0] = toString(line.getBill().getId());
				columns[1] = toString(line.getBill().getNumber());
				columns[2] = toString(line.getBill().getEffectiveDate());
				columns[3] = toString(line.getBill().getCreatedDate());
				
				columns[4] = toString(line.getBill().getMember().getId());
				columns[5] = toString(line.getBill().getDisplayName());
				columns[6] = toString(line.getBill().getDisplayAddress());
				
				columns[7] = toString(line.getBill().getPaymentMethod());
				columns[8] = toString(line.getBill().getStatus());
				columns[9] = toString(line.getQuantity());
				columns[10] = toString(line.getLabel());
				columns[11] = toString(line.getPriceTaxIncl());
				columns[12] = toString(line.getTotalTaxIncl());
				columns[13] = toString(line.getSubscription().getId());
				columns[14] = toString(line.getSubscription().getSubscriptionStartDate());
				columns[15] = toString(line.getSubscription().getSubscriptionEndDate());
				columns[16] = toString(line.getPeriodStart());
				columns[17] = toString(line.getPeriodEnd());
				columns[18] = toString(line.getTotalBookingOnPeriod());
				columns[19] = toString(line.getTotalBooking());
				columns[20] = toString(line.getBill().getTotalTaxIncl());
				
				writer.writeNext(columns, false);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	

	private static final String toString(Object value){
//		sb.append("\"").append(value == null ? "" : value).append("\"");
		if (value == null)
			return "";
		else if (value instanceof LocalDate) {
			return ((LocalDate) value).toString("dd/MM/yyyy");
		}
		else if (value instanceof DateTime) {
			return ((DateTime) value).toString("dd/MM/yyyy HH:mm:ss");
		}
		
		return value.toString().replaceAll("\n", " ");
	}
	
}
