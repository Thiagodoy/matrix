/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */


//    private Long nCdEmpresa;
//    private String sNrCnpj;
//    private String sNmEmpresa;
//    private String sNmFantasia;
//    private String sNmApelido;
//    private Long nCdTipoAgente;
//    private String sDsTipoAgente;






@Table
@Entity
@Data
public class Empresa {

    @Id
    @Column(name = "nCdEmpresa")
    private Long nCdEmpresa;

    @Column(name = "sNrCnpj")
    public String nrCnpj;

    @Column(name = "sNmEmpresa")
    private String empresa;

    @Column(name = "sNmFantasia")
    private String sNmFantasia;

    @Column(name = "tDtCadastro")
    private LocalDateTime tDtCadastro;

    @Column(name = "sDsUrl")
    private String sDsUrl;

    @Column(name = "nCdIdioma")
    private Long nCdIdioma;

    @Column(name = "nStEmpresa")
    private Long nStEmpresa;

    @Column(name = "sNmApelido")
    private String sNmApelido;

    @Column(name = "sCdUsuarioLiberacao")
    private String sCdUsuarioLiberacao;

    @Column(name = "bFlAceitaRegulamento")
    private Long bFlAceitaRegulamento;

    @Column(name = "sNrInscricaoMunicipal")
    private String sNrInscricaoMunicipal;

    @Column(name = "sNrInscricaoEstadual")
    private String sNrInscricaoEstadual;

    @Column(name = "sDsPastaIntegracao")
    private String sDsPastaIntegracao;

    @Column(name = "nIdLido")
    private Long nIdLido;

    @Column(name = "sDsConhecimentoCliente")
    private String sDsConhecimentoCliente;

    @Column(name = "sDsEscopoTrabalho")
    private String sDsEscopoTrabalho;

    @Column(name = "sNmContato")
    private String sNmContato;

    @Column(name = "sDsContatoEmail")
    private String sDsContatoEmail;

    @Column(name = "sDsContatoCargo")
    private String sDsContatoCargo;

    @Column(name = "sDsContatoFone")
    private String sDsContatoFone;

    @Column(name = "nCdTipoAgente")
    private Long nCdTipoAgente;

    @Column(name = "tDdDataReajuste")
    private LocalDateTime tDdDataReajuste;

    @Column(name = "nCdCnae")
    private Long nCdCnae;

    @Column(name = "nCdNaturezaJuridica")
    private Long nCdNaturezaJuridica;

    @Column(name = "nIdSuperSimples")
    private Long nIdSuperSimples;

    @Column(name = "sCdPaisIE")
    private String sCdPaisIE;

    @Column(name = "sCdEstadoIE")
    private String sCdEstadoIE;

    @Column(name = "sCdSAMP")
    private String sCdSAMP;

    @Column(name = "nCdSAP")
    private Long nCdSAP;

    @Column(name = "nCdANEEL")
    private Long nCdANEEL;

    @Column(name = "nCdAgenteCCEE")
    private Long nCdAgenteCCEE;

    @Column(name = "bFlGrupoEDP")
    private Long bFlGrupoEDP;

    @Column(name = "nNrNivelTensao")
    private Double nNrNivelTensao;

    @Column(name = "nQtDemandaContratadaPonta")
    private Double nQtDemandaContratadaPonta;

    @Column(name = "nQtDemandaContratadaForaPonta")
    private Double nQtDemandaContratadaForaPonta;

    @Column(name = "sDsGrupoTarifario")
    private String sDsGrupoTarifario;

    @Column(name = "sDsScoreCreditoEnertrade")
    private String sDsScoreCreditoEnertrade;

    @Column(name = "nCdMercadoAtuacao")
    private Long nCdMercadoAtuacao;

    @Column(name = "nCdCarteira")
    private Long nCdCarteira;

    @Column(name = "nCdDistribuidora")
    private Long nCdDistribuidora;

    @Column(name = "sCdUsuarioGestor")
    private String sCdUsuarioGestor;

    @Column(name = "nStIntegracao")
    private Long nStIntegracao;

    @Column(name = "sCdEmpresaCliente")
    private String sCdEmpresaCliente;

    @Column(name = "nCdSegmento")
    private Long nCdSegmento;

    @Column(name = "sDsLogo")
    private String sDsLogo;

    @Column(name = "nPcIncentivada")
    private Double nPcIncentivada;

    @Column(name = "bFlFilial")
    private Long bFlFilial;

    @Column(name = "sDsContatoDDD")
    private String sDsContatoDDD;

    @Column(name = "sDsContatoRamal")
    private String sDsContatoRamal;

    @Column(name = "sDsContatoFax")
    private String sDsContatoFax;

    @Column(name = "sDsContatoCaixaPostal")
    private String sDsContatoCaixaPostal;

    @Column(name = "dVlCapitalSocial")
    private Double dVlCapitalSocial;

    @Column(name = "tDtAlteracao")
    private LocalDateTime tDtAlteracao;

    @Column(name = "tDtLiberacao")
    private LocalDateTime tDtLiberacao;

    @Column(name = "nCdPorte")
    private Long nCdPorte;

    @Column(name = "bFlIntegracaoSEGMC")
    private Long bFlIntegracaoSEGMC;

    @Column(name = "tDtAlteracaoCapitalSocial")
    private LocalDateTime tDtAlteracaoCapitalSocial;

    @Column(name = "bFlRepresentante")
    private Long bFlRepresentante;

    @Column(name = "sDsEntidadeRegistradora")
    private String sDsEntidadeRegistradora;

    @Column(name = "sNrRegistroJuntaComercial")
    private String sNrRegistroJuntaComercial;

    @Column(name = "tDtRegistroJuntaComercial")
    private LocalDateTime tDtRegistroJuntaComercial;

    @Column(name = "sNrCnpjMatriz")
    private String sNrCnpjMatriz;

    @Column(name = "sDsRegProfRespTecnico")
    private String sDsRegProfRespTecnico;

    @Column(name = "sDsProtocoloCadastro")
    private String sDsProtocoloCadastro;

    @Column(name = "sCdCND")
    private String sCdCND;

    @Column(name = "tDtFinalInativo")
    private LocalDateTime tDtFinalInativo;

    @Column(name = "nIdTipoAdministracao")
    private Long nIdTipoAdministracao;

    @Column(name = "nIdNetting")
    private Long nIdNetting;

    @Column(name = "nVlValorGlobal")
    private Double nVlValorGlobal;

    @Column(name = "nCdMoeda")
    private Long nCdMoeda;

    @Column(name = "nVlAjusteMaximo")
    private Double nVlAjusteMaximo;

    @Column(name = "bFlRecadastroClasse")
    private Long bFlRecadastroClasse;

    @Column(name = "nCdSubmercado")
    private Long nCdSubmercado;

    @Column(name = "nCdIntegracaoSapRazaoEspecial")
    private Long nCdIntegracaoSapRazaoEspecial;

    @Column(name = "sNmIntegracaoSapEmpresa")
    private String sNmIntegracaoSapEmpresa;

    @Column(name = "sNmIntegracaoSapCentro")
    private String sNmIntegracaoSapCentro;

    @Column(name = "sNmIntegracaoSapCodigoSAPFornecedor")
    private String sNmIntegracaoSapCodigoSAPFornecedor;

    @Column(name = "sNmIntegracaoSapCodigoSAPCliente")
    private String sNmIntegracaoSapCodigoSAPCliente;

    @Column(name = "sNmIntegracaoSapSociedadeParceira")
    private String sNmIntegracaoSapSociedadeParceira;

    @Column(name = "nVlFatorDemanda")
    private Double nVlFatorDemanda;

    @Column(name = "bFlAgenteCadastradoCCEE")
    private Long bFlAgenteCadastradoCCEE;

    @Column(name = "bFlAgenteRepresentado")
    private Long bFlAgenteRepresentado;

    @Column(name = "nCdEmpresaCCEERepresentante")
    private Long nCdEmpresaCCEERepresentante;

    @Column(name = "nCdONS")
    private Long nCdONS;

    @Column(name = "sCdSAMPAcessante")
    private String sCdSAMPAcessante;

    @Column(name = "nCdAtivo")
    private String nCdAtivo;

    @Column(name = "nCdAtendimento")
    private Long nCdAtendimento;

    @Column(name = "nCdRepresentante")
    private Long nCdRepresentante;

    @Column(name = "sCdSAPPagamento")
    private String sCdSAPPagamento;

    @Column(name = "sCdSAPRecebimento")
    private String sCdSAPRecebimento;

    @Column(name = "sCdSAPRecebEncargo")
    private String sCdSAPRecebEncargo;

    @Column(name = "nCdAnexo")
    private Long nCdAnexo;

    @Column(name = "nQtRating")
    private Double nQtRating;

    @Column(name = "tDdRating")
    private LocalDateTime tDdRating;

    @Column(name = "sDsObsRating")
    private String sDsObsRating;

    @Column(name = "nCdSociedadeClienteERP")
    private Long nCdSociedadeClienteERP;

    @Column(name = "sDsSociedadeParceiraERP")
    private String sDsSociedadeParceiraERP;

    @Column(name = "nCdProcedAdverERP")
    private Long nCdProcedAdverERP;

    @Column(name = "nCdCondicaoPagamentoERP")
    private Long nCdCondicaoPagamentoERP;

    @Column(name = "sDsOrganizacaoCompras2000ERP")
    private String sDsOrganizacaoCompras2000ERP;

    @Column(name = "sDsOrganizacaoCompras2012ERP")
    private String sDsOrganizacaoCompras2012ERP;

    @Column(name = "nCdTipoIntegracaoERPEmissora")
    private Long nCdTipoIntegracaoERPEmissora;

    @Column(name = "tDtSincronizacao")
    private LocalDateTime tDtSincronizacao;
    
    
    public void teste(){
        
    }

}
