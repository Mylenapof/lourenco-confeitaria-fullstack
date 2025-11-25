import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProdutoService } from '@services/produto.service';
import { CategoriaService } from '@services/categoria.service';
import { Categoria } from '@models/categoria.model';
import { Produto } from '@models/produto.model';

@Component({
  selector: 'app-produto-form',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
    MatSnackBarModule
  ],
  templateUrl: './produto-form.component.html',
  styleUrls: ['./produto-form.component.scss']
})
export class ProdutoFormComponent implements OnInit {
  produtoForm: FormGroup;
  categorias: Categoria[] = [];
  loading = false;
  editando = false;
  produtoId: string | null = null;
  imagemPreview: string | null = null;

  constructor(
    private fb: FormBuilder,
    private produtoService: ProdutoService,
    private categoriaService: CategoriaService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.produtoForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      preco: ['', [Validators.required, Validators.min(0.01)]],
      ingredientes: ['', Validators.required],
      imagemUrl: ['', [Validators.required, Validators.pattern(/^https?:\/\/.+\.(jpg|jpeg|png|gif|webp)$/i)]],
      categoriaId: ['', Validators.required],
      disponivel: [true],
      destaque: [false]
    });
  }

  ngOnInit() {
    this.carregarCategorias();
    
    // Verificar se está editando
    this.produtoId = this.route.snapshot.paramMap.get('id');
    if (this.produtoId) {
      this.editando = true;
      this.carregarProduto(this.produtoId);
    }

    // Preview da imagem
    this.produtoForm.get('imagemUrl')?.valueChanges.subscribe(url => {
      if (url && this.isValidImageUrl(url)) {
        this.imagemPreview = url;
      } else {
        this.imagemPreview = null;
      }
    });
  }

  carregarCategorias() {
    this.categoriaService.listarTodos().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
      },
      error: (err) => console.error('Erro ao carregar categorias:', err)
    });
  }

  carregarProduto(id: string) {
    this.loading = true;
    this.produtoService.buscarPorId(id).subscribe({
      next: (produto) => {
        this.produtoForm.patchValue({
          nome: produto.nome,
          preco: produto.preco,
          ingredientes: produto.ingredientes,
          imagemUrl: produto.imagemUrl,
          categoriaId: produto.categoria.id,
          disponivel: produto.disponivel,
          destaque: produto.destaque
        });
        this.imagemPreview = produto.imagemUrl;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar produto:', err);
        this.snackBar.open('Erro ao carregar produto', 'OK', { duration: 3000 });
        this.loading = false;
        this.router.navigate(['/admin/produtos']);
      }
    });
  }

  isValidImageUrl(url: string): boolean {
    return /^https?:\/\/.+\.(jpg|jpeg|png|gif|webp)$/i.test(url);
  }

  onSubmit() {
    if (this.produtoForm.valid) {
      this.loading = true;

      const produtoData = {
        nome: this.produtoForm.value.nome,
        preco: parseFloat(this.produtoForm.value.preco),
        ingredientes: this.produtoForm.value.ingredientes,
        imagemUrl: this.produtoForm.value.imagemUrl,
        categoriaId: this.produtoForm.value.categoriaId,
        disponivel: this.produtoForm.value.disponivel,
        destaque: this.produtoForm.value.destaque
      };

      const operacao = this.editando 
        ? this.produtoService.atualizar(this.produtoId!, produtoData)
        : this.produtoService.criar(produtoData);

      operacao.subscribe({
        next: () => {
          this.snackBar.open(
            `Produto ${this.editando ? 'atualizado' : 'criado'} com sucesso!`,
            'OK',
            { duration: 3000, panelClass: ['snackbar-success'] }
          );
          this.router.navigate(['/admin/produtos']);
        },
        error: (err) => {
          console.error('Erro ao salvar produto:', err);
          this.snackBar.open(
            'Erro ao salvar produto. Verifique os dados.',
            'OK',
            { duration: 3000, panelClass: ['snackbar-error'] }
          );
          this.loading = false;
        }
      });
    } else {
      Object.keys(this.produtoForm.controls).forEach(key => {
        this.produtoForm.get(key)?.markAsTouched();
      });
    }
  }

  cancelar() {
    if (confirm('Deseja realmente cancelar? As alterações não serão salvas.')) {
      this.router.navigate(['/admin/produtos']);
    }
  }
}
