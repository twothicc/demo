package com.example.demo;

import com.example.demo.model.SerializeObj;
import com.example.demo.model.SerializedObj;
import com.example.demo.model.SerializedObj2;
import com.example.demo.repository.SerializeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import sg.gov.cpf.gpg.commonlib.dbengine.GPGDataSourceConfig;
import sg.gov.cpf.gpg.commonlib.eligibilitycommon.exception.DuplicateEligibilityException;
import sg.gov.cpf.gpg.commonlib.eligibilitycommon.model.Eligibility;
import sg.gov.cpf.gpg.commonlib.eligibilitycommon.service.EligibilityService;
import sg.gov.cpf.gpg.commonlib.transactioncommon.exception.DuplicateTransactionException;
import sg.gov.cpf.gpg.commonlib.transactioncommon.model.Transaction;
import sg.gov.cpf.gpg.commonlib.transactioncommon.service.TransactionService;

@SpringBootTest(
		properties = {
				"gpg.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=GPG;encrypt=true;TrustServerCertificate=true",
				"gpg.datasource.username=demoUser",
				"gpg.datasource.password=demo_pass"
		}
)
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private EligibilityService eligibilityService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private SerializeRepository serializeRepository;

	@BeforeEach
	public void resetSerializeRepositoryTable() {
		serializeRepository.deleteAll();
	}

	@Test
	void testEligibilityTransaction() throws DuplicateEligibilityException, DuplicateTransactionException {
		Eligibility eligibility = new Eligibility(
			"S9972421H", "AAA", new BigDecimal(100), "Y",
			null, "N", null, null,
				null, null, null, null,
				null, "Y", "N", null
		);
		eligibilityService.insertEligibility(eligibility);

		Transaction transaction = new Transaction(
				"S9972421H", "AAA", (short) 2024, new BigDecimal(100), null, "AAA",
				null, null, null, null, "AAA",
				new BigDecimal(0)
		);
		transactionService.insertTransaction(transaction);
	}

	@Test
	void testSerializeObj() {
		// First we use SerializeObj and serialize SerializedObj
		SerializedObj serializedObj = new SerializedObj(11, "test11");
		SerializeObj serializeObj = new SerializeObj("test12", serializedObj);

		SerializeObj result = serializeRepository.save(serializeObj);
		Optional<SerializeObj> savedSerializeObj = serializeRepository.findById(result.getId());
		assertTrue(savedSerializeObj.isPresent());

		SerializedObj obj = savedSerializeObj.get().getSerializedObj();
		assertNotNull(obj);

		System.out.println(obj);

		assertEquals(11, obj.getObj1());
		assertEquals("test11", obj.getObj2());
		System.out.println(savedSerializeObj.get().getSerializedContentBytes());

		// Now we try using the same SerializeObj but we serialize SerializedObj2
		SerializedObj2 serializedObj2 = new SerializedObj2(99, true);
		SerializeObj serializeObj2 = new SerializeObj("test99", serializedObj2);

		SerializeObj nextResult = serializeRepository.save(serializeObj2);
		Optional<SerializeObj> nextSavedSerializeObj = serializeRepository.findById(nextResult.getId());
		assertTrue(nextSavedSerializeObj.isPresent());

		SerializedObj2 obj2 = nextSavedSerializeObj.get().getSerializedObj2();
		assertNotNull(obj2);

		System.out.println(obj2);

		assertEquals(99, obj2.getObj1());
		assertTrue(obj2.getObj2());
		System.out.println(nextSavedSerializeObj.get().getSerializedContentBytes());
	}

}
