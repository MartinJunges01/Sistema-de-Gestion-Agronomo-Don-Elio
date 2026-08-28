# Bugs Identificados

> Los issues con ID oficial se encuentran en el Roadmap (oadmap_iteracion_3.md).
> Este archivo registra **deuda técnica nueva** detectada durante sesiones de desarrollo de la Iteración 3, pendiente de subir a GitHub para obtener su ID.

---

## [Resuelto] DT-017: Fallo en CI por falta de actualización en tests y SeedModule durante refactor (Issue351)
**Severidad:** 🔴 Bug Bloqueante
**Descripción:** Durante el refactor de CampaniaEntity para la Issue #351, se actualizaron los DAO tests pero se omitió actualizar SeedModule (lo que causaba un fallo de compilación en debug) y los tests unitarios en main que referenciaban la firma vieja, lo que rompió el CI.
**Resolución:** Resuelto en la rama Issue351 antes de mergear (commit fix(di) y fix(test)).

## [Resuelto] DT-018: PR #386 no actualiza el roadmap ni el CHANGELOG
**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Documentación
**Causa:** La rama eature/issue-350-costo-hectarea no incluye actualización del roadmap ni entrada en CHANGELOG.
**Resolución:** Se resolvieron durante el merge unificado de PRs, actualizando ambos archivos en main.

## [Resuelto] DT-019: CultivoCatalogoViewModelTest sin cobertura para flujos de error
**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Cultivos / Tests
**Causa:** El test de CultivoCatalogoViewModel cubre el happy path pero no los paths de error de CrearCultivoUseCase / EditarCultivoUseCase.
**Resolución:** Se resolvieron junto con DT-020 agregando tests unitarios a los UseCases.

## [Resuelto] DT-020: UseCases de Cultivos sin cobertura unitaria
**Severidad:** 🔵 Cobertura / Deuda Técnica
**Módulo:** Cultivos / Domain / Tests
**Causa:** El PR #388 agregó ObtenerCultivosUseCase, CrearCultivoUseCase, EditarCultivoUseCase, y EliminarCultivoUseCase pero sin tests unitarios propios.
**Resolución:** Se añadieron las clases de test correspondientes a main.

<!-- Plantilla para nuevos bugs:
## [PENDIENTE-ID] Título descriptivo del bug

**Severidad:** 🔴 Bug Bloqueante | 🟡 Bug Funcional | 🔵 UX / Deuda Técnica
**Módulo:** [Ej: Insumos / Tareas / Sincronización]
**Archivo afectado:** uta/del/archivo.kt

**Descripción**
Breve descripción del problema encontrado...

**Causa Raíz (Código)**
`kotlin
// Snippet del código problemático si se conoce
`

**Criterios de Aceptación**
- [ ] Criterio 1
- [ ] Criterio 2
-->

## [Resuelto] DT-021: ValidarDatosCosechaUseCase tiene logica redundante
**Severidad:** 🟡 Deuda Técnica
**Resolución:** Se eliminó el parámetro hectareas y la guarda inalcanzable durante el merge de la PR #395.

## [Pendiente] DT-022: ReportesViewModel inyecta repositorios directamente (Issue #398)
**Severidad:** 🔴 Deuda Técnica (Arquitectura)
**Descripción:** ReportesViewModel inyecta CampaniaInsumoRepository y CosechaRepository violando Clean Architecture.
**Criterios de Aceptación:** Crear ObtenerResumenFinancieroPorCampaniasUseCase y delegar.

## [Resuelto] DT-023: ObtenerEvolucionCultivoUseCase sin tests
**Severidad:** 🟡 Cobertura / Deuda Técnica
**Resolución:** Se agregó ObtenerEvolucionCultivoUseCaseTest con 4 casos luego del merge de la PR #394.

## [Resuelto] DT-024: plan_de_pruebas.md desactualizado
**Severidad:** 🔵 Documentación / Deuda Técnica
**Resolución:** Se agregaron los flujos GWT de PRs #395 y #396 al final de la iteración.
