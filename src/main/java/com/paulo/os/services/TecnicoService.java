package com.paulo.os.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paulo.os.domain.Tecnico;
import com.paulo.os.domain.dtos.TecnicoDTO;
import com.paulo.os.repositories.TecnicoRepository;
import com.paulo.os.services.exceptions.DataIntegratyViolationException;
import com.paulo.os.services.exceptions.ObjectNotFoundException;

@Service
public class TecnicoService {

	@Autowired
	private TecnicoRepository repository;

	/*
	 * Busca por ID
	 */
	public Tecnico findById(Integer id) {
		Optional<Tecnico> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Tecnico.class.getName()));
	}

	/*
	 * Listando todos os Tecnicos
	 */
	public List<Tecnico> findAll() {
		return repository.findAll();
	}

	/*
	 * Create
	 */
	public TecnicoDTO create(TecnicoDTO obj) {
		if (findByCpf(obj).getClass().equals(Tecnico.class))
			throw new DataIntegratyViolationException("CPF já cadastrado na base de dados!");
		
		Tecnico tecnico = new Tecnico(null, obj.getNome(), obj.getCpf());
		obj.getPerfis().forEach(x -> tecnico.addPerfil(x));
		return new TecnicoDTO(repository.save(tecnico));
	}

	/*
	 * Update
	 */
	public @Valid Tecnico update(@Valid TecnicoDTO obj, Integer id) {
		Tecnico oldObj = findById(id);

		if (findByCpf(obj) != null && findByCpf(obj).getId() != id) {
			throw new DataIntegratyViolationException("CPF já cadastrado na base de dados!");
		}

		oldObj.setCpf(obj.getCpf());
		oldObj.setNome(obj.getNome());
		obj.getPerfis().forEach(x -> oldObj.addPerfil(x));
		return repository.save(oldObj);
	}

	/*
	 * Busca por CPF
	 */
	private Tecnico findByCpf(TecnicoDTO objDTO) {
		Tecnico obj = repository.findByCpf(objDTO.getCpf());
		if (obj != null) {
			return obj;
		}
		return null;
	}

}
