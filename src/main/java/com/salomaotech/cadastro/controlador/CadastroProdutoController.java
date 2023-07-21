package com.salomaotech.cadastro.controlador;

import com.salomaotech.cadastro.modelo.Produto;
import com.salomaotech.cadastro.modelo.ProdutoRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CadastroProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    private final Path PASTA_RAIZ = Paths.get("./uploads");

    @GetMapping("/")
    public ModelAndView home() {

        ModelAndView view = new ModelAndView("Index");
        view.addObject("produto", new Produto());
        view.addObject("produtos", produtoRepository.findAll());
        return view;

    }

    @GetMapping("/produto/{id:.+}")
    public ModelAndView exibeProduto(@PathVariable long id) {

        ModelAndView view = new ModelAndView("Index");

        Optional<Produto> produto = produtoRepository.findById(id);

        if (!produto.isEmpty()) {

            view.addObject("produto", produto.get());

        } else {

            view.addObject("produto", new Produto());

        }

        return view;

    }

    @PostMapping("/cadastro")
    public ModelAndView cadastro(Produto produto, MultipartFile file) {

        try {

            Files.createDirectories(PASTA_RAIZ);
            Files.copy(file.getInputStream(), PASTA_RAIZ.resolve(file.getOriginalFilename()));
            produto.setImagem("/uploads/" + file.getOriginalFilename());

        } catch (Exception ex) {

        }

        produtoRepository.save(produto);

        return new ModelAndView("redirect:/");

    }

    @GetMapping("/uploads/{arquivo:.+}")
    public ResponseEntity<Resource> carregarImagem(@PathVariable String arquivo) {

        try {

            Resource resource = new UrlResource(PASTA_RAIZ.resolve(arquivo).toUri());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; arquivo=\"" + resource.getFilename() + "\"").body(resource);

        } catch (Exception ex) {

            return ResponseEntity.badRequest().build();

        }

    }

}
